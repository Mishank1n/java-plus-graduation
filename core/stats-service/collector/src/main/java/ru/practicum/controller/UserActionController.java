package ru.practicum.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.service.UserActionService;
import ru.yandex.practicum.grpc.user.action.UserActionProto;
import ru.yandex.practicum.stats.service.collector.UserActionControllerGrpc;


@Slf4j
@GrpcService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {

    UserActionService service;

    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            log.info("Пришел запрос о действиях пользователя с полями user_id {} event_id {} action_type {} timestamp {}",
                    request.getUserId(), request.getEventId(), request.getActionType(), request.getTimestamp());
            service.processUserAction(request);
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(
                    Status.INTERNAL.
                            withDescription(e.getLocalizedMessage())
                            .withCause(e)
            ));
        }
    }
}
