package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.dto.*;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.StatusRequest;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final EventMapper eventMapper;
    private final ViewService viewService;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    private final CommentMapper commentMapper;

    @Override
    public EventFullDto save(Long userId, NewEventDto newEventDto) {
        NewEventDto dto = checkNewEventDto(newEventDto);
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));

        Category category = categoryRepository
                .findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найден", newEventDto.getCategory())));
        Location location = locationRepository.save(locationMapper.toLocation(dto.getLocation()));

        Event event = eventMapper.toEvent(dto, location, category, user);
        event.setState(State.PENDING);
        if (event.getCreatedOn() == null) {
            event.setCreatedOn(LocalDateTime.now());
        }

        checkEventDate(event.getEventDate());

        return eventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getUsersEvents(Long userId, Integer from, Integer size) {

        return eventRepository.findAllByInitiator_Id(userId, PageRequest.of(from / size, size))
                .stream()
                .map(event -> eventMapper.toEventFullDto(event))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long userId, Long eventId) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));
        if (!event.getInitiator().getId().equals(userId)) {
            throw new BadRequestException("Запрос составлен некорректно");
        }


        return eventMapper.toEventFullDto(setComments(List.of(event)).get(0));
    }

    @Override
    public EventFullDto updateEventByUserId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        if (updateEventUserRequest.getEventDate() != null) {
            checkEventDate(updateEventUserRequest.getEventDate());
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Невозможно получить полную информацию о событии.");
        }
        if (event.getState().equals((State.PUBLISHED))) {
            throw new ConflictException("Изменить можно только отмененные события.");
        }
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
            }
        }
        return eventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        return mapToEventsFullDto(setComments(eventRepository
                .getEvents(PageRequest.of(from / size, size), users, states, categories, rangeStart, rangeEnd)
                .stream()
                .collect(Collectors.toList())));
    }

    @Override
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));


        if (updateEventAdminRequestDto.getTitle() != null) {
            event.setTitle(updateEventAdminRequestDto.getTitle());
        }
        if (updateEventAdminRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequestDto.getAnnotation());
        }
        if (updateEventAdminRequestDto.getDescription() != null) {
            event.setDescription(updateEventAdminRequestDto.getDescription());
        }
        if (updateEventAdminRequestDto.getCategory() != null) {
            Category category = categoryRepository
                    .findById(updateEventAdminRequestDto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найдена", updateEventAdminRequestDto.getCategory())));
            event.setCategory(category);
        }
        if (updateEventAdminRequestDto.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequestDto.getEventDate());
        }
        if (updateEventAdminRequestDto.getLocation() != null) {
            event.setLocation(event.getLocation());
        }

        if (updateEventAdminRequestDto.getEventDate() != null) {
            checkEventDate(updateEventAdminRequestDto.getEventDate());
            event.setEventDate(updateEventAdminRequestDto.getEventDate());
        }

        if (updateEventAdminRequestDto.getPaid() != null) {
            event.setPaid(updateEventAdminRequestDto.getPaid());
        }
        if (updateEventAdminRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequestDto.getParticipantLimit());
        }
        if (updateEventAdminRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequestDto.getRequestModeration());
        }

        if (updateEventAdminRequestDto.getStateAction() != null) {
            switch (updateEventAdminRequestDto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState().equals(State.PENDING)) {
                        event.setState(State.PUBLISHED);
                        event.setPublishedOn(LocalDateTime.now());
                    } else {
                        throw new ConflictException("Событие можно публиковать, только если оно в состоянии ожидания.");
                    }
                    break;
                case REJECT_EVENT:
                    if (event.getState().equals(State.PENDING)) {
                        event.setState(State.CANCELED);
                    } else {
                        throw new ConflictException("Событие можно отклонить, только если оно еще не опубликовано.");
                    }
                    break;
            }
        }


        return eventMapper.toEventFullDto(setComments(List.of(eventRepository.save(event))).get(0));

    }

    @Override
    public List<EventShortDto> getAllEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest httpServletRequest) {

        checkStartEndTime(rangeStart, rangeEnd);
        List<Event> events = eventRepository.searchEvent(text, categories, paid,
                        rangeStart,
                        rangeEnd,
                        onlyAvailable,
                        PageRequest.of(from / size, size))
                .stream()
                .collect(Collectors.toList());
        createHit(httpServletRequest);

        return mapToEventShortDto(setComments(events));
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие не найдено");
        }
        createHit(httpServletRequest);

        return mapToEventsFullDto(setComments(List.of(event))).get(0);
    }

    private void checkEventDate(LocalDateTime eventDate) {
        LocalDateTime dateTime = LocalDateTime.now().plusHours(2);
        if (eventDate.isBefore(dateTime)) {
            throw new BadRequestException("Дата события должна быть не менее чем через два часа.");
        }
    }

    private NewEventDto checkNewEventDto(NewEventDto newEventDto) {
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }
        return newEventDto;
    }


    private List<EventShortDto> mapToEventShortDto(List<Event> events) {
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<EventShortDto> dtos = events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());

        Map<Long, Long> eventsViews = viewService.getViews(events);
        Map<Long, Long> confirmedRequests = getConfirmedRequests(eventIds);

        dtos.forEach(el -> {
            el.setViews(eventsViews.getOrDefault(el.getId(), 0L));
            el.setConfirmedRequests(confirmedRequests.getOrDefault(el.getId(), 0L));
        });

        return dtos;
    }

    private Map<Long, Long> getConfirmedRequests(List<Long> eventsId) {
        List<ParticipationRequest> confirmedRequests = requestRepository
                .findAllByStatusAndEventIdIn(StatusRequest.CONFIRMED, eventsId);

        return confirmedRequests.stream()
                .collect(Collectors.groupingBy(request -> request.getEvent().getId()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
    }

    private void createHit(HttpServletRequest request) {
        viewService.createHit(request);
    }


    private List<EventFullDto> mapToEventsFullDto(List<Event> events) {
        Map<Long, Long> views = viewService.getViews(events);
        Map<Long, Long> confirmedRequests = viewService.getConfirmedRequests(events);
        return events.stream()
                .map((event) -> eventMapper.toEventFullDto(
                        event,
                        confirmedRequests.getOrDefault(event.getId(), 0L),
                        views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    private void checkStartEndTime(LocalDateTime start, LocalDateTime end) {
        if (end != null && start != null) {
            if (end.isBefore(start)) {
                throw new BadRequestException("Время окончания не должно быть раньше времени начала.");
            }
        }
    }

    private List<Event> setComments(List<Event> events) {
        List<Comment> getComments = commentRepository.findAll();
        for (Event event : events) {
            event.setComments(getComments.stream().filter(comment -> comment.getEvent().getId().equals(event.getId())
                            && event.getState().equals(State.PUBLISHED))
                    .map(comment -> commentMapper.toCommentDto(comment))
                    .collect(Collectors.toList()));
        }
        return events;
    }

}
