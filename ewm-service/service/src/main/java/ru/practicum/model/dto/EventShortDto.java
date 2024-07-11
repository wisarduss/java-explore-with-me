package ru.practicum.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.utils.Constants.DATE_TIME_PATTERN;

@Data
public class EventShortDto {

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    private Long id;

    private UserDto initiator;

    private Boolean paid;

    private String title;

    private Long views;

    private List<CommentDto> comments;

}