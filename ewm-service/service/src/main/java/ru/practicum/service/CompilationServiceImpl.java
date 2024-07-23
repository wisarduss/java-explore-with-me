package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.Compilation;
import ru.practicum.entity.Event;
import ru.practicum.exception.IdNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.model.dto.CompilationDto;
import ru.practicum.model.dto.NewCompilationRequestDto;
import ru.practicum.model.dto.UpdateCompilationRequestDto;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.utils.CustomPageRequest.pageRequestOf;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CompilationDto save(NewCompilationRequestDto newCompilationRequestDto) {
        List<Event> events = null;
        if (newCompilationRequestDto.getEvents() != null && !newCompilationRequestDto.getEvents().isEmpty()) {
            events = eventRepository.findAllById(newCompilationRequestDto.getEvents());
        }

        Compilation result = compilationRepository.save(CompilationMapper
                .toCompilation(newCompilationRequestDto, events));
        log.debug("добавлена подборка событий");
        return modelMapper.map(result, CompilationDto.class);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequestDto body) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new IdNotFoundException("Подборки события с id = " + compId + " не найдено"));

        if (body.getPinned() != null && body.getPinned() != compilation.getPinned()) {
            compilation.setPinned(body.getPinned());
        }
        if (body.getTitle() != null && !body.getTitle().equals(compilation.getTitle())) {
            compilation.setTitle(body.getTitle());
        }
        if (isEventsUpdateRequired(compilation, body)) {
            List<Event> events = eventRepository.findAllById(body.getEvents());
            compilation.setEvents(events);
        }

        Compilation saved = compilationRepository.saveAndFlush(compilation);
        log.debug("обновлена подборка событий");
        return modelMapper.map(saved, CompilationDto.class);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new IdNotFoundException("Подборки события с id = " + compId + " не найдено"));
        log.debug("подборка событий удалена");
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> findAllWithParams(Boolean pinned, Integer from, Integer size) {
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageRequestOf(from, size)).stream()
                    .map(it -> modelMapper.map(it, CompilationDto.class))
                    .collect(Collectors.toList());
        }
        log.debug("Получены все подборки событий с параметрами");
        return compilationRepository.findAll(pageRequestOf(from, size)).stream()
                .map(it -> modelMapper.map(it, CompilationDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto findById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new IdNotFoundException("Подборки события с id = " + compId + " не найдено"));
        log.debug("получена подборка событий по id");
        return modelMapper.map(compilation, CompilationDto.class);
    }

    private boolean isEventsUpdateRequired(Compilation compilation,
                                           UpdateCompilationRequestDto updateCompilationRequestDto) {
        return updateCompilationRequestDto.getEvents() != null &&
                !updateCompilationRequestDto.getEvents().stream().sorted()
                        .equals(compilation.getEvents().stream()
                                .map(Event::getId)
                                .sorted()
                                .collect(Collectors.toList()));
    }
}
