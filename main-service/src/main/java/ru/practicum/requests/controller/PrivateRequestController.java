package ru.practicum.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.ParticipationRequestService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PrivateRequestController {
    private final ParticipationRequestService participationRequestService;

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        log.info("Добавление запроса от текущего пользователя на участие в событии {} {}", userId, eventId);
        ParticipationRequestDto participationRequestDto = participationRequestService.createRequest(userId, eventId);
        log.error("{}",participationRequestDto);
        return participationRequestDto;
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto updateRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Отмена своего запроса на участие в событии");
        return participationRequestService.updateRequestStatus(userId, requestId);
    }

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsByUser(@PathVariable Long userId) {
        log.info("Получение информации о заявках текущего пользователя на участие в чужих событиях");
        return participationRequestService.getRequestsByUser(userId);
    }
}
