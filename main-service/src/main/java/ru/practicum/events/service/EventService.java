package ru.practicum.events.service;

import ru.practicum.events.dto.*;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto save(Long userId, NewEventDto newEventDto);

    List<EventFullDto> getUsersEvents(Long userId, Integer from, Integer size);

    EventFullDto getEventById(Long userId, Long eventId);

    EventFullDto updateEventByUserId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto);

    List<EventShortDto> getAllEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                     Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest httpServletRequest);

    EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest);
}
