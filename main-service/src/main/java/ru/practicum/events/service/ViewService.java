package ru.practicum.events.service;

import ru.practicum.events.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface ViewService {

        void createHit(HttpServletRequest request);

        Map<Long, Long> getViews(List<Event> events);

        Map<Long, Long> getConfirmedRequests(List<Event> events);

}
