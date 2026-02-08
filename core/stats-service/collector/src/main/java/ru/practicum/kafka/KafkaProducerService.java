package ru.practicum.kafka;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.mapper.UserActionProtoToAvroMapper;
import ru.yandex.practicum.grpc.user.action.UserActionProto;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KafkaProducerService {

    KafkaTemplate<Long, Object> kafkaTemplate;
    String userActionTopic = "stats.user-actions.v1";

    public void sendUserAction(UserActionProto userAction) {
        try {
            UserActionAvro userActionAvro = UserActionProtoToAvroMapper.toAvro(userAction);
            kafkaTemplate.send(userActionTopic, userAction.getUserId(), userActionAvro);
        } catch (Exception e) {
            log.error("Error sending user action to Kafka: {}", e.getMessage(), e);
            throw e;
        }
    }
}
