package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.*;
import ru.practicum.events.service.EventService;
import ru.practicum.exception.ConflictException;
import ru.practicum.requests.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.service.ParticipationRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PrivateEventController {

    private final EventService eventService;
    private final ParticipationRequestService participationRequestService;

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventFullDto saveEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Создание нового события {}", newEventDto);

        return eventService.save(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventFullDto> getAllUsersEvents(@PathVariable Long userId,
                                                @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Получение событий, добавленных текущим пользователем");
        return eventService.getUsersEvents(userId, from, size);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getUsersEventById(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        log.info("Получение полной информации о событии добавленном текущим пользователем");
        return eventService.getEventById(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEventByUserId(@PathVariable Long userId,
                                            @PathVariable Long eventId,
                                            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info("Изменение события добавленного текущим пользователем");
        return eventService.updateEventByUserId(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequests(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        log.info("Получение информации о запросах на участие в событии текущего пользователя");
        return participationRequestService.getParticipationRequests(userId, eventId);
    }


    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResultDto updateParticipationRequest(@PathVariable Long userId,
                                                                        @PathVariable Long eventId,
                                                                        @RequestBody EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto) {
        log.info("Изменение статуса (подтверждена, отменена) заявок на участие в событии текущего пользователя");
        try {
            return participationRequestService.updateParticipationRequest(userId, eventId, eventRequestStatusUpdateRequestDto);
        } catch (ConflictException e) {
            throw new ConflictException(e.getMessage());
        }
    }
}
