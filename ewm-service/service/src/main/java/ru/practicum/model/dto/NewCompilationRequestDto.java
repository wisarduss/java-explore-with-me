package ru.practicum.model.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class NewCompilationRequestDto {

    private List<Long> events;
    private Boolean pinned;

    @NotBlank
    @Length(min = 1, max = 50)
    private String title;

}
