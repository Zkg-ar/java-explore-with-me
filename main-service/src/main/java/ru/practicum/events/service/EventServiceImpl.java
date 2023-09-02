package ru.practicum.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.Constant;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
//import ru.practicum.client.EventClient;
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
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
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
    //private final EventClient client;
    private final EventMapper eventMapper;

    @Override
    public EventFullDto save(Long userId, NewEventDto newEventDto) {
        checkNewEventDto(newEventDto);
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));

        Category category = categoryRepository
                .findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найден", newEventDto.getCategory())));
        Location location = locationRepository.save(locationMapper.toLocation(newEventDto.getLocation()));

        Event event = eventMapper.toEvent(newEventDto, location, category, user);
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
        return eventMapper.toEventFullDto(event);
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
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<State> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {

        return eventRepository
                .getEvents(users, states, categories, rangeStart == null ? LocalDateTime.now() : LocalDateTime.parse(rangeStart, Constant.FORMATTER), rangeEnd == null ? LocalDateTime.now() : LocalDateTime.parse(rangeEnd, Constant.FORMATTER), PageRequest.of(from / size, size))
                .stream()
                .map(event -> eventMapper.toEventFullDto(event))
                .collect(Collectors.toList());
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


        return eventMapper.toEventFullDto(eventRepository.save(event));

    }

    @Override
    public List<EventShortDto> getAllEvents(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size, HttpServletRequest httpServletRequest) {
        List<Event> events = eventRepository.searchEvent(text, categories, paid, State.PUBLISHED, PageRequest.of(from / size, size));
        List<EventShortDto> eventShortDtos = events.stream()
                .filter(event -> rangeStart != null ?
                        event.getEventDate().isAfter(LocalDateTime.parse(rangeStart, Constant.FORMATTER)) :
                        event.getEventDate().isAfter(LocalDateTime.now())
                                && rangeEnd != null ? event.getEventDate().isBefore(LocalDateTime.parse(rangeEnd,
                                Constant.FORMATTER)) :
                                event.getEventDate().isBefore(LocalDateTime.MAX))
                .map(event -> eventMapper.toEventShortDto(event))
                .collect(Collectors.toList());

        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE":
                    eventShortDtos = eventShortDtos
                            .stream()
                            .sorted(Comparator.comparing(EventShortDto::getEventDate))
                            .collect(Collectors.toList());
                    break;
                case "VIEWS":
                    eventShortDtos = eventShortDtos
                            .stream()
                            .sorted(Comparator.comparing(EventShortDto::getViews))
                            .collect(Collectors.toList());
                    break;
                default:
                    throw new BadRequestException("Сортировка возможна только по просмотрам или дате события.");
            }
        }
        //createHit(httpServletRequest);
        return eventShortDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));

        //createHit(httpServletRequest);
        return eventMapper.toEventFullDto(event);
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

//    private void createHit(HttpServletRequest request) {
//        client.createHit(request);
//    }

}
