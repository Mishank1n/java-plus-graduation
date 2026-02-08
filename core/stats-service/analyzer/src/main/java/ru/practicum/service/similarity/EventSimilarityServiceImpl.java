package ru.practicum.service.similarity;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.model.EventSimilarity;
import ru.practicum.repository.EventSimilarityRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EventSimilarityServiceImpl implements EventSimilarityService {

    EventSimilarityRepository repository;

    @Override
    public void collect(EventSimilarityAvro event) {
        Optional<EventSimilarity> eventSimilarityOptional = repository.findByFirstEventAndSecondEvent(event.getEventA(), event.getEventB());
        if (eventSimilarityOptional.isPresent()) {
            EventSimilarity eventSimilarity = eventSimilarityOptional.get();
            eventSimilarity.setSimilarity(eventSimilarity.getSimilarity());
            repository.save(eventSimilarity);
        } else {
            repository.save(EventSimilarity.builder()
                    .firstEvent(event.getEventA())
                    .secondEvent(event.getEventB())
                    .similarity(event.getScore())
                    .registrationTime(LocalDateTime.ofInstant(event.getTimestamp(), ZoneId.systemDefault()))
                    .build());
        }
    }
}
