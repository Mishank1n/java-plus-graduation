package ru.practicum.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.service.recommendation.RecommendationService;
import ru.yandex.practicum.grpc.requests.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.requests.RecommendedEventProto;
import ru.yandex.practicum.grpc.requests.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.requests.UserPredictionsRequestProto;
import ru.yandex.practicum.stats.service.dashboard.RecommendationsControllerGrpc;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class RecommendationController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService recommendationService;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("GRPC method getRecommendationsForUser started request={} ", request);
        try {
            recommendationService.getRecommendationsForUser(request).forEach(
                    responseObserver::onNext
            );
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)));
        }
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("GRPC method getSimilarEvents started request={} ", request);
        try {
            recommendationService.getSimilarEvents(request).forEach(
                    responseObserver::onNext
            );
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)));
        }
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request, StreamObserver<RecommendedEventProto> responseObserver) {
        log.info("GRPC method getInteractionsCount started request={} ", request);
        try {
            recommendationService.getInteractionsCount(request).forEach(
                    responseObserver::onNext
            );
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.INTERNAL.withDescription(e.getLocalizedMessage()).withCause(e)));
        }
    }
}