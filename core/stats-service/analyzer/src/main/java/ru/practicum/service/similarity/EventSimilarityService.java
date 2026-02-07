package ru.practicum.service.similarity;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

public interface EventSimilarityService {
    void collect(EventSimilarityAvro event);
}
