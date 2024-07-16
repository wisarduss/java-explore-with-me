package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.entity.Compilation;
import ru.practicum.entity.Event;
import ru.practicum.model.dto.NewCompilationRequestDto;

import java.util.Collections;
import java.util.List;

@UtilityClass
public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationRequestDto dto, List<Event> eventList) {
        return Compilation.builder()
                .events(eventList != null ? eventList : Collections.emptyList())
                .pinned(dto.getPinned() != null ? dto.getPinned() : false)
                .title(dto.getTitle())
                .build();
    }
}
