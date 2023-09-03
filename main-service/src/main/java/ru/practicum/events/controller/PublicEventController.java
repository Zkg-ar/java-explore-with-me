package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.Constant;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventController {

    private final EventService eventService;


    @GetMapping
    public List<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                         @RequestParam(required = false) List<Long> categoryIds,
                                         @RequestParam(required = false) Boolean paid,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = Constant.FORMAT) LocalDateTime rangeStart,
                                         @RequestParam(required = false) @DateTimeFormat(pattern = Constant.FORMAT) LocalDateTime rangeEnd,
                                         @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                         @RequestParam(required = false) String sort,
                                         @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(value = "size", defaultValue = "10") Integer size,
                                         HttpServletRequest httpServletRequest) {
        log.info("Получение всех записей");
        return eventService.getAllEvents(text, categoryIds, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, httpServletRequest);
    }

    @GetMapping("/{id}")
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        log.info("Получение записи по id");
        return eventService.getPublicEventById(id, httpServletRequest);
    }
}