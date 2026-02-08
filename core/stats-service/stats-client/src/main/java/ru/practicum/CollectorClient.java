package ru.practicum;

import com.google.protobuf.Timestamp;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.user.action.ActionTypeProto;
import ru.yandex.practicum.grpc.user.action.UserActionProto;

import java.time.Instant;

@Component
public class CollectorClient {
    @GrpcClient("collector")
    private ru.yandex.practicum.stats.service.collector.UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public void sendUserAction(Long userId, Long eventId, ActionTypeProto actionType) {
        Instant time = Instant.now();
        client.collectUserAction(UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionType)
                .setTimestamp(Timestamp.newBuilder().setSeconds(time.getEpochSecond()).setNanos(time.getNano()).build())
                .build());
    }
}