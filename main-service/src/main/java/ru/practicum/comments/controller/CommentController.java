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

    /*
     * Создание комментария
     * userId - id автора
     * eventId - id события к которому написан комментарий
     **/
    @PostMapping("/comments/{userId}/{eventId}")
    public CommentDto createComment(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody NewCommentDto commentDto) {
        return commentService.createComment(userId, eventId, commentDto);
    }

    /*
     *
     * Получение списка всех комментарий определенного события
     *
     **/

    @GetMapping("/comments/{eventId}")
    public List<CommentDto> getAllComments(@PathVariable Long eventId,
                                           @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                           @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return commentService.getAllComments(eventId, from, size);
    }

    /*
     *
     * Пользователь может отредактировать свой комментарий
     *
     * */

    @PatchMapping("/comments/{userId}/{commentId}")
    public CommentDto patchComment(@PathVariable Long userId,
                                   @PathVariable Long commentId,
                                   @RequestBody NewCommentDto commentDto) {
        return commentService.patchComment(userId, commentId, commentDto);
    }

    /*
     *
     * Администратор может удалить комментарии пользователя
     *
     * */
    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminDeleteComment(@PathVariable Long commentId) {
        commentService.adminDeleteComment(commentId);
    }

    /*
     *
     * Пользователь может удалить свой комментарий
     *
     * */
    @DeleteMapping("/public/comments/{userId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void userDeleteComment(@PathVariable Long userId,
                                  @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
    }

}
