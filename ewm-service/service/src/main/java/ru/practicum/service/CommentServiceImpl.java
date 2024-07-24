package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.Comment;
import ru.practicum.entity.Event;
import ru.practicum.entity.User;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.IdNotFoundException;
import ru.practicum.model.EventStatus;
import ru.practicum.model.dto.CommentDto;
import ru.practicum.model.dto.NewCommentDto;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto body) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IdNotFoundException("Событие с id = " + eventId + " не найдено"));

        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            throw new ConflictException();
        }

        Comment comment = Comment.builder()
                .author(user)
                .event(event)
                .text(body.getText())
                .build();
        log.debug("Коммеентарий создан");
        return modelMapper.map(commentRepository.save(comment), CommentDto.class);
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto body) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IdNotFoundException("Пользователь с id = " + userId + " не найден"));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IdNotFoundException("Событие с id = " + eventId + " не найдено"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IdNotFoundException("Комментарий с id = " + commentId + " не найден"));

        if (!event.getState().equals(EventStatus.PUBLISHED)
                || !comment.getAuthor().getId().equals(userId)
        ) {
            throw new ConflictException();
        }

        comment.setText(body.getText());
        log.debug("Комментарий изменен");
        return modelMapper.map(commentRepository.saveAndFlush(comment), CommentDto.class);
    }

    @Override
    public void deleteComment(Long eventId, Long commentId) {
        eventRepository.findById(eventId)
                .orElseThrow(() -> new IdNotFoundException("События с id = " + eventId + " не найдено"));

        commentRepository.findById(commentId)
                .orElseThrow(() -> new IdNotFoundException("Комментарий с id = " + commentId + " не найден"));
        log.debug("Комментарий удален");
        commentRepository.deleteById(commentId);
    }

}
