package ru.practicum.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.UpdateEventAdminRequestDto;
import ru.practicum.events.model.State;
import ru.practicum.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @GetMapping("/admin/events")
    public List<EventFullDto> getEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                               @RequestParam(required = false) List<State> states,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) String rangeStart,
                                               @RequestParam(required = false) String rangeEnd,
                                               @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateByAdmin(@PathVariable Long eventId, @Valid @RequestBody UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        return eventService.updateByAdmin(eventId, updateEventAdminRequestDto);
    }


}
