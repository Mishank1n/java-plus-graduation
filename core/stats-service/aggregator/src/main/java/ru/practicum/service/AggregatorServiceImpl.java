package ru.practicum.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AggregatorServiceImpl implements AggregatorService {

    // Event -> (User -> Weight)
    private final Map<Long, Map<Long, Double>> eventUserWeight = new HashMap<>();

    // Event -> SumWeight
    private final Map<Long, Double> eventTotalWeight = new HashMap<>();

    // (EventA < EventB) -> S_min
    private final Map<Long, Map<Long, Double>> eventPairsWeight = new HashMap<>();

    @Override
    public List<EventSimilarityAvro> process(final UserActionAvro action) {
        log.info("Получено сообщение: {}", action);

        Long eventId = action.getEventId();
        Long userId = action.getUserId();

        Double newWeight = resolveActionWeight(action);
        Double oldWeight = fetchUserWeight(eventId, userId);

        // Если вес не увеличился — ничего не пересчитываем
        if (newWeight <= oldWeight) {
            return List.of();
        }

        persistUserWeight(eventId, userId, newWeight);
        applyEventTotalDelta(eventId, oldWeight, newWeight);

        return recalcSimilaritiesForUser(eventId, userId, oldWeight, action.getTimestamp());
    }

    /* ======================= RESOLVE WEIGHT ======================= */

    private Double resolveActionWeight(UserActionAvro action) {
        return switch (action.getActionType()) {
            case VIEW -> 0.4D;
            case REGISTER -> 0.8D;
            case LIKE -> 1D;
        };
    }

    /* ======================= USER WEIGHT ======================= */

    private Double fetchUserWeight(Long eventId, Long userId) {
        return eventUserWeight.containsKey(eventId)
                && eventUserWeight.get(eventId).containsKey(userId)
                ? eventUserWeight.get(eventId).get(userId)
                : 0D;
    }

    private void persistUserWeight(Long eventId, Long userId, Double weight) {
        log.info("Сохраняем вес: event={}, user={}, weight={}", eventId, userId, weight);
        eventUserWeight.computeIfAbsent(eventId, e -> new HashMap<>()).put(userId, weight);
    }

    /* ======================= TOTAL EVENT WEIGHT ======================= */

    private void applyEventTotalDelta(Long eventId, Double oldWeight, Double newWeight) {
        log.info("Обновляем сумму весов события: old={}, new={}", oldWeight, newWeight);
        eventTotalWeight.put(
                eventId,
                eventTotalWeight.getOrDefault(eventId, 0D) + newWeight - oldWeight
        );
    }

    /* ======================= SIMILARITY ======================= */

    private List<EventSimilarityAvro> recalcSimilaritiesForUser(
            Long eventId,
            Long userId,
            Double oldWeight,
            Instant timestamp
    ) {
        List<EventSimilarityAvro> result = new ArrayList<>();

        eventUserWeight.entrySet().stream()
                .filter(entry -> isComparableEvent(entry, eventId, userId))
                .map(Map.Entry::getKey)
                .forEach(otherEventId -> {

                    updatePairMinWeight(eventId, otherEventId, userId, oldWeight);

                    result.add(
                            buildSimilarityRecord(eventId, otherEventId, timestamp)
                    );
                });

        return result;
    }

    private boolean isComparableEvent(Map.Entry<Long, Map<Long, Double>> entry,
                                      Long eventId,
                                      Long userId) {
        return !entry.getKey().equals(eventId)
                && entry.getValue().containsKey(userId);
    }

    /* ======================= S_MIN UPDATE ======================= */

    private void updatePairMinWeight(Long eventId,
                                     Long otherEventId,
                                     Long userId,
                                     Double oldUserWeight) {

        Long first = Math.min(eventId, otherEventId);
        Long second = Math.max(eventId, otherEventId);

        eventPairsWeight.putIfAbsent(first, new HashMap<>());

        Double previous = eventPairsWeight.get(first).getOrDefault(second, 0D);

        Double updated =
                previous
                        - Math.min(oldUserWeight, fetchUserWeight(otherEventId, userId))
                        + Math.min(fetchUserWeight(eventId, userId),
                        fetchUserWeight(otherEventId, userId));

        eventPairsWeight.get(first).put(second, updated);
    }

    /* ======================= SCORE ======================= */

    private EventSimilarityAvro buildSimilarityRecord(Long eventId,
                                                      Long otherEventId,
                                                      Instant timestamp) {

        Long first = Math.min(eventId, otherEventId);
        Long second = Math.max(eventId, otherEventId);

        Double score = computeScore(first, second);

        return EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(score)
                .setTimestamp(timestamp)
                .build();
    }

    private Double computeScore(Long first, Long second) {

        Double minSum = eventPairsWeight.get(first).get(second);
        Double normA = Math.sqrt(eventTotalWeight.get(first));
        Double normB = Math.sqrt(eventTotalWeight.get(second));

        Double score = minSum / (normA * normB);

        log.info("Сходство событий: first={}, second={}", first, second);
        log.info("minSum={}, normA={}, normB={}, score={}", minSum, normA, normB, score);

        return score;
    }
}