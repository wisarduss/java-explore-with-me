package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.*;
import ru.practicum.service.CommentService;
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class PrivateEventController {

    private final EventService eventService;
    private final RequestService requestService;
    private final CommentService commentService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findEventsByUser(@PathVariable Long userId,
                                                @RequestParam(required = false, defaultValue = "0") @Min(0) Integer from,
                                                @RequestParam(required = false, defaultValue = "10") @Min(1) Integer size
    ) {
        return eventService.findEventsByUser(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId, @Valid @RequestBody NewEventDto eventDto) {
        return eventService.create(userId, eventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto get(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.findUserEventById(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateUserEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequestDto updateEventUserRequestDto) {
        return eventService.updateUserEvent(userId, eventId, updateEventUserRequestDto);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findEventRequests(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {

        return requestService.findRequestsByUserEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequest(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        return requestService.updateRequestsByUserEvent(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @PostMapping("/{userId}/events/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody NewCommentDto comment
    ) {

        return commentService.createComment(userId, eventId, comment);
    }

    @PatchMapping("/{userId}/events/{eventId}/comment/{commentId}")
    public CommentDto updateComment(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody NewCommentDto comment
    ) {
        return commentService.updateComment(userId, eventId, commentId, comment);
    }

}
