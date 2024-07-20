package ru.practicum.service;

import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.NewCompilationRequestDto;
import ru.practicum.model.dto.UpdateCompilationRequestDto;

import java.util.List;

public interface CompilationService {

    CompilationDto save(NewCompilationRequestDto body);

    CompilationDto update(Long compId, UpdateCompilationRequestDto body);

    void delete(Long compId);

    List<CompilationDto> findAllWithParams(Boolean pinned, Integer from, Integer size);

    CompilationDto findById(Long compId);
}
