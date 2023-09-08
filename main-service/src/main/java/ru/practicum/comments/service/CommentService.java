package ru.practicum.comments.service;

import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    List<CommentDto> getAllComments(Long eventId, Integer from, Integer size);

    CommentDto patchComment(Long userId, Long commentId, NewCommentDto newCommentDto);

    void adminDeleteComment(Long commentId);

    void deleteComment(Long userId, Long commentId);
}
