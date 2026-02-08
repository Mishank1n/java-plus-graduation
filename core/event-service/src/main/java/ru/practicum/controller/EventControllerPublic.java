package ru.practicum.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.model.EventParam;
import ru.practicum.service.EventService;
import ru.yandex.practicum.grpc.user.action.ActionTypeProto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@Validated
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventControllerPublic {
    final EventService eventService;
    static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    static final String EVENT_ID_PATH = "/{eventId}";
    static final String USER_ID_HEADER = "X-EWM-USER-ID";

    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(name = "text", required = false) String text,
                                         @RequestParam(name = "categories", required = false) List<Long> categories,
                                         @RequestParam(name = "paid", required = false) Boolean paid,
                                         @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeStart,
                                         @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = DATE_TIME_PATTERN) LocalDateTime rangeEnd,
                                         @RequestParam(name = "onlyAvailable", required = false) Boolean onlyAvailable,
                                         @RequestParam(name = "sort", required = false) String sort,
                                         @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero int from,
                                         @RequestParam(name = "size", defaultValue = "10") @Positive int size,
                                         HttpServletRequest request) {
        EventParam eventParam = EventParam.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .request(request)
                .build();

        log.info("Выполнен запрос получения всех событий");
        return eventService.getEvents(eventParam);
    }

    @GetMapping(EVENT_ID_PATH)
    public EventFullDto getEvent(@PathVariable(name = "eventId") Long eventId, @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Выполнен запрос получения события с id={}", eventId);
        EventFullDto eventFullDto = eventService.getEvent(eventId, userId);
        saveHit(userId, eventId);
        return eventFullDto;
    }

    @GetMapping("/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsRecommendations(@RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на получение списка рекомендаций пользователя {}", userId);
        return eventService.getEventsRecommendations(userId);
    }

    @PutMapping("/{eventId}/like")
    @ResponseStatus(HttpStatus.CREATED)
    public void likeEvent(@PathVariable("eventId") Long eventId,
                          @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на лайк ивента {} от пользователя {}", eventId, userId);
        eventService.likeEvent(eventId, userId);
    }

    private void saveHit(Long userId, Long eventId) {
        eventService.sendUserAction(userId, eventId, ActionTypeProto.ACTION_VIEW);
    }
}
