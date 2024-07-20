package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.NewCompilationRequestDto;
import ru.practicum.model.dto.UpdateCompilationRequestDto;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto save(@Valid @RequestBody final NewCompilationRequestDto body) {
        return compilationService.save(body);
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable Long compId,
                                 @Valid @RequestBody final UpdateCompilationRequestDto updateCompilationRequestDto) {

        return compilationService.update(compId, updateCompilationRequestDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        compilationService.delete(compId);
    }
}
