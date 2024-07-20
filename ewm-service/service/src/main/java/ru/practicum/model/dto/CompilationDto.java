package ru.practicum.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CompilationDto {

    private List<EventShortDto> events;

    @NotNull
    private Long id;

    @NotNull
    private boolean pinned;

    @NotNull
    private String title;

}
