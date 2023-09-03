//package ru.practicum.events.service;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import ru.practicum.client.EventClient;
//import ru.practicum.dto.ViewStatsDto;
//import ru.practicum.events.model.Event;
//import ru.practicum.requests.repository.RequestRepository;
//
//
//import javax.servlet.http.HttpServletRequest;
//import java.time.LocalDateTime;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional(readOnly = true)
//@RequiredArgsConstructor
//public class ViewServiceImpl implements ViewService {
//
//    private final RequestRepository requestRepository;
//    private final EventClient client;
//    private final ObjectMapper mapper = new ObjectMapper();
//
//
//    @Override
//    public void createHit(HttpServletRequest request) {
//        client.createHit(request);
//    }
//
//
//    @Override
//    public Map<Long, Long> getViews(List<Event> events) {
//        Map<Long, Long> views = new HashMap<>();
//        List<Event> publishedEvents = getPublished(events);
//        if (events.isEmpty()) {
//            return views;
//        }
//        Optional<LocalDateTime> minPublishedOn = getMinPublishedOn(publishedEvents);
//        if (minPublishedOn.isPresent()) {
//            LocalDateTime start = minPublishedOn.get();
//            LocalDateTime end = LocalDateTime.now();
//            List<String> uris = getURIs(publishedEvents);
//            List<ViewStatsDto> stats = getStats(start, end, uris, true);
//            putStats(views, stats);
//        }
//        return views;
//    }
//
//    @Override
//    public Map<Long, Long> getConfirmedRequests(List<Event> events) {
//        List<Long> eventsId = getPublished(events).stream()
//                .map(Event::getId)
//                .collect(Collectors.toList());
//        Map<Long, Long> requestStats = new HashMap<>();
//        if (!eventsId.isEmpty()) {
//            requestRepository.findConfirmedRequests(eventsId)
//                    .forEach(stat -> requestStats.put(stat.getEventId(), stat.getConfirmedRequests()));
//        }
//        return requestStats;
//    }
//
//    private Optional<LocalDateTime> getMinPublishedOn(List<Event> publishedEvents) {
//        return publishedEvents.stream()
//                .map(Event::getPublishedOn)
//                .filter(Objects::nonNull)
//                .min(LocalDateTime::compareTo);
//    }
//
//    private List<String> getURIs(List<Event> publishedEvents) {
//        return publishedEvents.stream()
//                .map(Event::getId)
//                .map(id -> ("/events/" + id))
//                .collect(Collectors.toList());
//    }
//
//    private void putStats(Map<Long, Long> views, List<ViewStatsDto> stats) {
//        stats.forEach(stat -> {
//            Long eventId = Long.parseLong(stat.getUri()
//                    .split("/", 0)[2]);
//            views.put(eventId, views.getOrDefault(eventId, 0L) + stat.getHits());
//        });
//    }
//
//    private List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
//        ResponseEntity<Object> response = client.getStats(start, end, uris, unique);
//        try {
//            return Arrays.asList(mapper.readValue(mapper.writeValueAsString(response.getBody()), ViewStatsDto[].class));
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//
//    private List<Event> getPublished(List<Event> events) {
//        return events.stream()
//                .filter(event -> event.getPublishedOn() != null)
//                .collect(Collectors.toList());
//    }
//}