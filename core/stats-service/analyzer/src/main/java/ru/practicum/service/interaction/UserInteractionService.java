package ru.practicum.service.interaction;

import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface UserInteractionService {
    void collect(UserActionAvro event);
}
