package ru.practicum;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.requests.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.requests.RecommendedEventProto;
import ru.yandex.practicum.grpc.requests.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.requests.UserPredictionsRequestProto;
import ru.yandex.practicum.stats.service.dashboard.RecommendationsControllerGrpc;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecommendationsClient {
    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    public List<RecommendedEventProto> getRecommendationsForUser(Long userId, Long maxResults) {
        return Lists.newArrayList(client.getRecommendationsForUser(UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build()));
    }

    public List<RecommendedEventProto> getSimilarEvents(Long userId, Long eventId, Long maxResults) {
        return Lists.newArrayList(client.getSimilarEvents(SimilarEventsRequestProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setMaxResults(maxResults)
                .build()));
    }

    public List<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        return Lists.newArrayList(client.getInteractionsCount(InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build()));
    }
}