package ru.practicum.service.interaction;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.UserInteraction;
import ru.practicum.repository.UserInteractionRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserInteractionServiceImpl implements UserInteractionService {

    UserInteractionRepository repository;

    @Override
    public void collect(UserActionAvro event) {
        Optional<UserInteraction> userInteractionOptional = repository.findByEventIdAndUserId(event.getEventId(), event.getUserId());
        if (userInteractionOptional.isPresent()) {
            UserInteraction userInteraction = userInteractionOptional.get();
            userInteraction.setRating(Math.max(userInteraction.getRating(), getRatingOfAction(event.getActionType())));
            repository.save(userInteraction);
        } else {
            repository.save(UserInteraction.builder()
                    .userId(event.getUserId())
                    .eventId(event.getEventId())
                    .rating(getRatingOfAction(event.getActionType()))
                    .registrationTime(LocalDateTime.ofInstant(event.getTimestamp(), ZoneId.systemDefault()))
                    .build());
        }
    }

    private double getRatingOfAction(ActionTypeAvro actionTypeAvro) {
        return switch (actionTypeAvro) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;

        };
    }
}
