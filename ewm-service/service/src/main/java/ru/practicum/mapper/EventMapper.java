package ru.practicum.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.modelmapper.ModelMapper;
import ru.practicum.entity.Category;
import ru.practicum.entity.Event;
import ru.practicum.model.dto.*;

import java.util.Collections;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ModelMapper modelMapper = new ModelMapper();

    public static Event toEvent(NewEventDto dto, Category category) throws JsonProcessingException {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(objectMapper.writeValueAsString(dto.getLocation()))
                .paid(dto.getPaid() != null ? dto.getPaid() : false)
                .participantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0)
                .requestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration() : true)
                .title(dto.getTitle())
                .views(0L)
                .confirmedRequests(0)
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) throws JsonProcessingException {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(modelMapper.map(event.getCategory(), CategoryDto.class))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .id(event.getId())
                .initiator(modelMapper.map(event.getInitiator(), UserDto.class))
                .location(modelMapper.map(objectMapper.readValue(event.getLocation(), LocationDto.class),
                        LocationDto.class))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.isRequestModeration())
                .state(event.getState().name())
                .title(event.getTitle())
                .views(event.getViews())
                .comments(
                        event.getComments() == null ? Collections.emptyList() : event.getComments().stream()
                                .map(it -> modelMapper.map(it, CommentDto.class))
                                .collect(Collectors.toList()))
                .build();
    }
}
