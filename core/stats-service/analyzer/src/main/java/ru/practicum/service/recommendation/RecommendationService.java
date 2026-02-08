package ru.practicum.service.recommendation;

import ru.yandex.practicum.grpc.requests.InteractionsCountRequestProto;
import ru.yandex.practicum.grpc.requests.RecommendedEventProto;
import ru.yandex.practicum.grpc.requests.SimilarEventsRequestProto;
import ru.yandex.practicum.grpc.requests.UserPredictionsRequestProto;

public interface RecommendationService {
    Iterable<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto request);

    Iterable<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto request);

    Iterable<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto request);
}
