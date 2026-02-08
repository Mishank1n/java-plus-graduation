package ru.practicum.service;

import ru.practicum.event.dto.*;
import ru.practicum.model.EventParam;
import ru.practicum.request.dto.RequestDto;
import ru.yandex.practicum.grpc.user.action.ActionTypeProto;

import java.util.List;
import java.util.Set;

public interface EventService {
    List<EventShortDto> getEvents(EventParam p);

    List<EventFullDto> getEventsAdmin(EventParam p);

    EventFullDto getEvent(Long eventId, Long userId);

    EventFullDto create(NewEventDto newEventDto, Long userId);

    List<EventShortDto> getAllByUser(Long userId, int from, int size);

    EventFullDto getByUserAndId(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest updateRequest);

    List<RequestDto> getParticipationInfo(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest statusUpdateRequest);

    EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest adminRequest);

    Set<EventShortDto> getEventsByIds(Set<Long> eventIds);

    EventFullDto getEvent(Long eventId);

    boolean existsByCategory(Long category);

    void updateConfirmedRequests(Long eventId, Integer newAmount);

    void deleteEventsByAuthor(Long authorId);

    List<EventShortDto> getEventsRecommendations(Long userId);

    void likeEvent(Long eventId, Long userId);

    void sendUserAction(Long userId, Long eventId, ActionTypeProto actionTypeProto);
}
