package ru.practicum.service;

import ru.practicum.model.dto.CommentDto;
import ru.practicum.model.dto.NewCommentDto;

public interface CommentService {

    CommentDto createComment(Long userId, Long eventId, NewCommentDto body);

    CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto body);

    void deleteComment(Long eventId, Long commentId);
}
