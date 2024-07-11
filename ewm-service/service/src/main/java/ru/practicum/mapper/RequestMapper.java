package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.entity.Request;
import ru.practicum.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.model.dto.ParticipationRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class RequestMapper {

    public static ParticipationRequestDto toRequestDTO(Request request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .id(request.getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().name())
                .build();
    }

    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(
            List<Request> confirmedRequests, List<Request> rejectedRequests) {
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream()
                        .map(RequestMapper::toRequestDTO)
                        .collect(Collectors.toList()))
                .rejectedRequests(rejectedRequests.stream()
                        .map(RequestMapper::toRequestDTO)
                        .collect(Collectors.toList()))
                .build();
    }
}
