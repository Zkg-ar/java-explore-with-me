package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));

        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Вы не можете комментировать данное событие, поскольку оно не опубликовано");
        }

        Comment comment = commentMapper.toComment(newCommentDto);
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());

        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllComments(Long eventId, Integer from, Integer size) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Событие с id = %d не найдено", eventId));
        }
        return commentRepository
                .findAllByEvent_Id(eventId, PageRequest.of(from / size, size))
                .stream()
                .map(comment -> commentMapper.toCommentDto(comment))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto patchComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Вы не являетесь автором комментария");
        }

        comment.setText(newCommentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void adminDeleteComment(Long commentId) {
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        commentRepository.delete(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ConflictException("Вы не можете удалить комментарий, поскольку не являетесь его автором");
        }

        commentRepository.delete(comment);

    }
}
