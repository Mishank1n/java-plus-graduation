package ru.practicum.model.mapper;

import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.yandex.practicum.grpc.user.action.UserActionProto;

import java.time.Instant;

public class UserActionProtoToAvroMapper {

    public static UserActionAvro toAvro(UserActionProto userActionProto) {
        UserActionAvro.Builder userActionAvro = UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setTimestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(), userActionProto.getTimestamp().getNanos()));
        switch (userActionProto.getActionType()) {
            case ACTION_VIEW -> userActionAvro.setActionType(ActionTypeAvro.VIEW);
            case ACTION_LIKE -> userActionAvro.setActionType(ActionTypeAvro.LIKE);
            case ACTION_REGISTER -> userActionAvro.setActionType(ActionTypeAvro.REGISTER);
        }
        return userActionAvro.build();
    }
}
