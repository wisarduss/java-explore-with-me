package ru.practicum.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.practicum.model.AdminEventRequestStatus;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

import static ru.practicum.utils.Constants.DATE_TIME_PATTERN;

@Data
public class UpdateEventAdminRequestDto {

    @Length(min = 20, max = 2000)
    private String annotation;

    @Positive
    private Long category;

    @Length(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private AdminEventRequestStatus stateAction;

    @Length(min = 3, max = 120)
    private String title;

}
