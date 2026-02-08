package ru.practicum.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.practicum.kafka.KafkaProducerService;
import ru.yandex.practicum.grpc.user.action.UserActionProto;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserActionServiceImpl implements UserActionService {
    KafkaProducerService kafkaProducerService;

    @Override
    public void processUserAction(UserActionProto request) {
        kafkaProducerService.sendUserAction(request);
    }
}
