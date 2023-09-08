package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comments/{userId}/{eventId}")
    public CommentDto createComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody NewCommentDto commentDto) {
        log.info("Создание комментария пользователем к событию");
        return commentService.createComment(userId, eventId, commentDto);
    }

    @GetMapping("/comments/{eventId}")
    public List<CommentDto> getAllComments(@PathVariable Long eventId,
                                           @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("Получение всех комментариев события");
        return commentService.getAllComments(eventId, from, size);
    }

    @PatchMapping("/comments/{userId}/{commentId}")
    public CommentDto patchComment(@PathVariable Long userId,
                                   @PathVariable Long commentId,
                                   @RequestBody NewCommentDto commentDto) {
        log.info("Пользователь редактирует комментарий");
        return commentService.patchComment(userId, commentId, commentDto);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteComment(@PathVariable Long commentId) {
        log.info("Администратор удалил комментарий пользователя");
        commentService.adminDeleteComment(commentId);
    }

    @DeleteMapping("/public/comments/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userDeleteComment(@PathVariable Long userId,
                                  @PathVariable Long commentId) {
        log.info("Удаление пользователем комментария.");
        commentService.deleteComment(userId, commentId);
    }

}
