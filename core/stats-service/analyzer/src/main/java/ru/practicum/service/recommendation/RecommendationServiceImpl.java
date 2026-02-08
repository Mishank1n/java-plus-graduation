package ru.practicum.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import ru.practicum.repository.EventSimilarityRepository;
import ru.practicum.repository.UserInteractionRepository;
import ru.yandex.practicum.grpc.requests.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.requests.RecommendedEventProto;
import ru.yandex.practicum.grpc.requests.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.requests.UserPredictionsRequestProto;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final EventSimilarityRepository eventSimilarityRepository;
    private final UserInteractionRepository userInteractionRepository;


    @Override
    public List<RecommendedEventProto> getInteractionsCount(final InteractionsCountRequestProto request) {
        return userInteractionRepository.getInteractionsSumByEventIds(request.getEventIdList()).stream()
                .map(rec -> RecommendedEventProto.newBuilder()
                        .setEventId(rec.eventId())
                        .setScore(rec.score())
                        .build())
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getSimilarEvents(final SimilarEventsRequestProto request) {
        return eventSimilarityRepository.getSimilarEvents(request.getUserId(), request.getEventId(), Limit.of((int) request.getMaxResults())).stream()
                .map(rec -> RecommendedEventProto.newBuilder()
                        .setEventId(rec.eventId())
                        .setScore(rec.score())
                        .build())
                .toList();
    }

    @Override
    public List<RecommendedEventProto> getRecommendationsForUser(final UserPredictionsRequestProto request) {
        return eventSimilarityRepository.getSimilarEventIdsForUser(request.getUserId(), Limit.of((int) request.getMaxResults())).stream()
                .map(eventId -> RecommendedEventProto.newBuilder()
                        .setEventId(eventId)
                        .setScore(eventSimilarityRepository.calculateRating(request.getUserId(), eventId).orElse(0D))
                        .build())
                .toList();
    }
}