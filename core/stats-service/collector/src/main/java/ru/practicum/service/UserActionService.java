package ru.practicum.service;

import ru.yandex.practicum.grpc.user.action.UserActionProto;

public interface UserActionService {
    void processUserAction(UserActionProto request);
}
