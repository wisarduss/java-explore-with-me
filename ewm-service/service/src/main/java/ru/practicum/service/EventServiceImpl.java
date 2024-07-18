package ru.practicum.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.entity.Category;
import ru.practicum.entity.Event;
import ru.practicum.entity.User;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.IdNotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.*;
import ru.practicum.model.dto.*;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.utils.CustomPageRequest.pageRequestOf;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final StatsClient statsClient;


    @SneakyThrows
    @Override
    public List<EventFullDto> findAdminEventsByParams(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        Specification<Event> specification = Specification.where(null);
        if (users != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> root.get("initiator").get("id").in(users));
        }
        if (states != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> root.get("state").as(String.class).in(states));
        }
        if (categories != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> root.get("category").get("id").in(categories));
        }
        if (rangeStart != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(
                            root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"),
                            rangeEnd));
        }

        List<Event> events = eventRepository.findAll(specification, PageRequest.of(from / size, size))
                .getContent();

        List<EventFullDto> result = new ArrayList<>();

        for (Event event : events) {
            EventFullDto eventFullDTO = EventMapper.toEventFullDto(event);
            result.add(eventFullDTO);
        }

        return result;
    }

    @SneakyThrows
    @Override
    public List<EventShortDto> findPublicEventsByParams(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sort, Integer from, Integer size, HttpServletRequest request) {

        Specification<Event> specification = Specification.where(null);

        specification = specification.and(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("state"),
                        EventStatus.PUBLISHED));

        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")),
                            "%" + text.toLowerCase() + "%"),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                            "%" + text.toLowerCase() + "%")));
        }

        if (categories != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> root.get("category").get("id").in(categories));
        }

        if (paid != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("paid"), paid));
        }

        LocalDateTime startDateTime = Objects.requireNonNullElse(rangeStart, LocalDateTime.now());
        specification = specification.and(
                (root, query, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get("eventDate"),
                        startDateTime));

        if (rangeEnd != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.lessThan(root.get("eventDate"),
                            rangeEnd));

            if (rangeStart.isAfter(rangeEnd)) {
                throw new ValidationException();
            }
        }

        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and(
                    (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(
                            root.get("participantLimit"), 0));
        }
        PageRequest pageRequest = pageRequestOf(from, size);
        if (sort != null) {
            if (sort.equals(EventSortType.EVENT_DATE)) {
                pageRequest = pageRequestOf(from, size, Sort.by("eventDate"));
            } else {
                pageRequest = pageRequestOf(from, size, Sort.by("views").descending());
            }
        }

        List<Event> events = eventRepository.findAll(specification, pageRequest).getContent();

        statsClient.hit(request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());

        ResponseEntity<Object> response = getEventsViewStats(events);

        List<EventViewStats> list = objectMapper.readValue(
                objectMapper.writeValueAsString(response.getBody()),
                new TypeReference<>() {
                });

        Map<Long, Long> eventsHits = getEventsHits(list);

        for (Event event : events) {
            event.setViews(eventsHits.get(event.getId()));
        }

        return events.stream().map(it -> modelMapper.map(it, EventShortDto.class))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    @Transactional
    public EventFullDto update(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IdNotFoundException("событие с id = " + eventId + " не найдено"));

        if (!event.getState().equals(EventStatus.PENDING)) {
            throw new ConflictException();
        }

        Category category = null;
        if (updateEventAdminRequestDto.getCategory() != null) {
            category = categoryRepository.findById(updateEventAdminRequestDto.getCategory())
                    .orElseThrow(() -> new IdNotFoundException("Категория с именем = "
                            + updateEventAdminRequestDto.getCategory() + " не найдено"));
            event.setCategory(category);
        }

        if (updateEventAdminRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequestDto.getAnnotation());
        }
        if (updateEventAdminRequestDto.getDescription() != null) {
            event.setDescription(updateEventAdminRequestDto.getDescription());
        }
        if (updateEventAdminRequestDto.getEventDate() != null) {
            if (LocalDateTime.now().isAfter(updateEventAdminRequestDto.getEventDate())) {
                throw new ValidationException();
            }
            if (checkEventDate(updateEventAdminRequestDto.getEventDate())) {
                throw new ConflictException();
            }
            event.setEventDate(updateEventAdminRequestDto.getEventDate());
        }
        if (updateEventAdminRequestDto.getLocation() != null) {
            event.setLocation(objectMapper.writeValueAsString(updateEventAdminRequestDto.getLocation()));
        }
        if (updateEventAdminRequestDto.getPaid() != null) {
            event.setPaid(updateEventAdminRequestDto.getPaid());
        }
        if (updateEventAdminRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequestDto.getParticipantLimit());
        }
        if (updateEventAdminRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequestDto.getRequestModeration());
        }
        if (updateEventAdminRequestDto.getStateAction() != null) {
            EventStatus status = updateEventAdminRequestDto.getStateAction().equals(AdminEventRequestStatus.PUBLISH_EVENT)
                    ? EventStatus.PUBLISHED : EventStatus.CANCELED;
            event.setState(status);
        }
        if (updateEventAdminRequestDto.getTitle() != null) {
            event.setTitle(updateEventAdminRequestDto.getTitle());
        }

        Event result = eventRepository.saveAndFlush(event);

        return EventMapper.toEventFullDto(result);
    }

    @SneakyThrows
    @Override
    public EventFullDto findById(Long eventId, HttpServletRequest request) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IdNotFoundException("Событие с id = " + eventId + " не найдено"));

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new IdNotFoundException("Событие с id = " + eventId + " не найдено");
        }

        String uri = request.getRequestURI();
        statsClient.hit(uri, request.getRemoteAddr(), LocalDateTime.now());

        ResponseEntity<Object> response = statsClient.getStats(event.getCreatedOn(),
                event.getEventDate(), new String[]{uri}, true);

        List<EventViewStats> list = objectMapper.readValue(
                objectMapper.writeValueAsString(response.getBody()), new TypeReference<>() {
                });

        if (!list.isEmpty()) {
            event.setViews(Long.valueOf(list.get(0).getHits()));
        }

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> findEventsByUser(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        return eventRepository.findAllByInitiatorId(userId, pageRequestOf(from, size)).stream()
                .map(it -> modelMapper.map(it, EventShortDto.class))
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        if (LocalDateTime.now().isAfter(newEventDto.getEventDate())) {
            throw new ValidationException();
        }
        if (checkEventDate(newEventDto.getEventDate())) {
            throw new ConflictException();
        }

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new IdNotFoundException("Категория с именем = " + newEventDto.getCategory() + " не найден"));

        Event event = EventMapper.toEvent(newEventDto, category);

        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventStatus.PENDING);

        Event result = eventRepository.saveAndFlush(event);

        return EventMapper.toEventFullDto(result);
    }

    @SneakyThrows
    @Override
    public EventFullDto findUserEventById(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IdNotFoundException("Событие с id = " + eventId + " не найдено"));

        if (!event.getInitiator().equals(user)) {
            throw new IdNotFoundException("Событие с id = " + eventId + " не найдено");
        }

        return EventMapper.toEventFullDto(event);
    }

    @Override
    @SneakyThrows
    @Transactional
    public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequestDto updateEventUserRequestDto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IdNotFoundException("Событие с id = " + eventId + " не найдено"));

        if (event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException();
        }

        if (updateEventUserRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequestDto.getCategory())
                    .orElseThrow(() -> new IdNotFoundException("Категория с именем = "
                            + updateEventUserRequestDto.getCategory() + " не найдено"));
            event.setCategory(category);
        }

        if (updateEventUserRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequestDto.getAnnotation());
        }
        if (updateEventUserRequestDto.getDescription() != null) {
            event.setDescription(updateEventUserRequestDto.getDescription());
        }
        if (updateEventUserRequestDto.getEventDate() != null) {
            if (checkEventDate(updateEventUserRequestDto.getEventDate())) {
                throw new ConflictException();
            }
            event.setEventDate(updateEventUserRequestDto.getEventDate());
        }
        if (updateEventUserRequestDto.getPaid() != null) {
            event.setPaid(updateEventUserRequestDto.getPaid());
        }
        if (updateEventUserRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequestDto.getParticipantLimit());
        }
        if (updateEventUserRequestDto.getTitle() != null) {
            event.setTitle(updateEventUserRequestDto.getTitle());
        }
        if (updateEventUserRequestDto.getLocation() != null) {
            event.setLocation(objectMapper.writeValueAsString(updateEventUserRequestDto.getLocation()));
        }
        if (updateEventUserRequestDto.getStateAction() != null) {
            EventStatus status =
                    updateEventUserRequestDto.getStateAction().equals(UserEventRequestStatus.CANCEL_REVIEW) ? EventStatus.CANCELED
                            : EventStatus.PENDING;
            event.setState(status);
        }

        Event result = eventRepository.saveAndFlush(event);

        return EventMapper.toEventFullDto(result);
    }

    private boolean checkEventDate(LocalDateTime eventDate) {
        return Duration.between(LocalDateTime.now(), eventDate).toHours() < 2L;
    }

    private ResponseEntity<Object> getEventsViewStats(List<Event> events) {
        String[] uri = getStatsUrisByEvents(events);
        LocalDateTime start = getStartDateTimeForStatsView(events);
        return statsClient.getStats(start, LocalDateTime.now(), uri, true);
    }

    private LocalDateTime getStartDateTimeForStatsView(List<Event> events) {
        return events.stream()
                .map(Event::getCreatedOn)
                .sorted()
                .findFirst()
                .orElseThrow();
    }

    private String[] getStatsUrisByEvents(List<Event> events) {
        return events.stream()
                .map(it -> "/events/" + it.getId())
                .toArray(String[]::new);
    }

    private Map<Long, Long> getEventsHits(List<EventViewStats> list) {
        Map<Long, Long> hits = new HashMap<>();
        for (EventViewStats eventViewStats : list) {
            String[] temp = eventViewStats.getUri().split("/");
            Long eventId = Long.parseLong(temp[temp.length - 1]);
            hits.put(eventId, Long.valueOf(eventViewStats.getHits()));
        }
        return hits;
    }
}
