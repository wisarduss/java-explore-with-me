package ru.practicum.service;

import ru.practicum.model.EventSortType;
import ru.practicum.model.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> findAdminEventsByParams(
            List<Long> users, List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<EventShortDto> findPublicEventsByParams(
            String text, List<Long> categories, Boolean paid,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sort,
            Integer from, Integer size, HttpServletRequest request);

    EventFullDto update(Long eventId, UpdateEventAdminRequestDto body);

    EventFullDto findById(Long eventId, HttpServletRequest request);

    List<EventShortDto> findEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto create(Long userId, NewEventDto body);

    EventFullDto findUserEventById(Long userId, Long eventId);

    EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventUserRequestDto);
}
