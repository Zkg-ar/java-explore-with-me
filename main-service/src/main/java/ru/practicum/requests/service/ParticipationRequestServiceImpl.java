package ru.practicum.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.events.service.ViewService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.requests.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.mapper.ParticipationRequestMapper;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.StatusRequest;
import ru.practicum.requests.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestMapper participationRequestMapper;

    private final ViewService viewService;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        if (requestRepository.findByRequester_IdAndEvent_Id(userId, eventId) != null) {
            throw new ConflictException("Нельзя добавить повторный запрос.");
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Собыитие с id = %d не найдено", eventId)));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователль с id = %d не найден", userId)));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может отправлять запрос на участие.");
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Данное событие не опубликовано.");
        }

        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= requestRepository.countAllByEventIdAndStatus(eventId, StatusRequest.CONFIRMED)) {
            throw new ConflictException("Лимит превышен");
        }
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setRequester(user);
        participationRequest.setEvent(event);


        if (event.getRequestModeration().equals(true) && event.getParticipantLimit() > 0) {
            participationRequest.setStatus(StatusRequest.PENDING);
        } else {
            participationRequest.setStatus(StatusRequest.CONFIRMED);
        }

        return participationRequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Override
    public ParticipationRequestDto updateRequestStatus(Long userId, Long requestId) {
        ParticipationRequest participationRequest = requestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %d не найден", requestId)));

        participationRequest.setStatus(StatusRequest.CANCELED);

        return participationRequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
        return requestRepository.findAllByRequester_Id(userId).stream().map(participationRequest -> participationRequestMapper.toParticipationRequestDto(participationRequest)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getParticipationRequests(Long userId, Long eventId) {
        return requestRepository.findAllByEvent_Initiator_IdAndEvent_Id(userId, eventId).stream().map(participationRequest -> participationRequestMapper.toParticipationRequestDto(participationRequest)).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResultDto updateParticipationRequest(Long userId, Long eventId,
                                                                        EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));


        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найдено", userId)));


//        if (!Objects.equals(user, event.getInitiator())) {
//            throw new ForbiddenException("Вы не являетесь владельцем события");
//        }

        ParticipationRequest templateSearchRequestParticipation = new ParticipationRequest();
        templateSearchRequestParticipation.setEvent(event);
        templateSearchRequestParticipation.setStatus(StatusRequest.CONFIRMED);
        long requestConfirmedCount = requestRepository.count(Example.of(templateSearchRequestParticipation));
        if (event.getParticipantLimit() != 0) {
            if (requestConfirmedCount >= event.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит запросов на участие");
            }
        }

        // заявки на участие
        List<ParticipationRequest> requests = requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());

        requests.stream()
                .filter(e -> e.getStatus() != StatusRequest.PENDING)
                .findFirst()
                .ifPresent(e -> {
                    throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
                });

        if (!event.getRequestModeration() && event.getParticipantLimit() == 0) {
            List<ParticipationRequestDto> confirmedRequests = requests.stream()
                    .peek(e -> e.setStatus(StatusRequest.CONFIRMED))
                    .map(participationRequest -> participationRequestMapper.toParticipationRequestDto(participationRequest))
                    .collect(Collectors.toList());

            return new EventRequestStatusUpdateResultDto(confirmedRequests,
                    new ArrayList<>());
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (eventRequestStatusUpdateRequest.getStatus() == StatusRequest.REJECTED) {
            rejectedRequests = requests.stream()
                    .peek(e -> e.setStatus(StatusRequest.REJECTED))
                    .map(participationRequest -> participationRequestMapper.toParticipationRequestDto(participationRequest))
                    .collect(Collectors.toList())
            ;
        }

        if (eventRequestStatusUpdateRequest.getStatus() == StatusRequest.CONFIRMED) {
            confirmedRequests = requests.stream()
                    .limit(event.getParticipantLimit() - requestConfirmedCount)
                    .peek(e -> e.setStatus(StatusRequest.CONFIRMED))
                    .map(participationRequest -> participationRequestMapper.toParticipationRequestDto(participationRequest))
                    .collect(Collectors.toList());

            rejectedRequests = requests.stream()
                    .skip(event.getParticipantLimit() - requestConfirmedCount)
                    .peek(e -> e.setStatus(StatusRequest.CONFIRMED))
                    .map(participationRequest -> participationRequestMapper.toParticipationRequestDto(participationRequest))
                    .collect(Collectors.toList());
        }

        return new EventRequestStatusUpdateResultDto(confirmedRequests,
                rejectedRequests);
    }
//    @Override
//    public EventRequestStatusUpdateResultDto updateParticipationRequest(Long userId, Long eventId, EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto) {
//        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException(String.format("Событие с id = %d не найдено", eventId)));
//
//
//        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найдено", userId)));
//
//
//        List<Long> ids = eventRequestStatusUpdateRequestDto.getRequestIds();
//        StatusRequest state = eventRequestStatusUpdateRequestDto.getStatus();
//
//        List<ParticipationRequestDto> confirmedList = new ArrayList<>();
//        List<ParticipationRequestDto> rejectedList = new ArrayList<>();
//
//        List<ParticipationRequest> requests = requestRepository.findAllByEvent_Id(eventId);
//
//        Long confirmRequests = viewService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) +
//                requests.size();
//        if (event.getParticipantLimit() != 0 && confirmRequests >= event.getParticipantLimit()) {
//            throw new ConflictException("Лимит участников уже заполнен.");
//        }
//
//
//        if (ids != null) {
//            for (Long id : ids) {
//                ParticipationRequest participationRequest = requests.stream().filter(participationRequest1 -> participationRequest1.getId().equals(id)).findFirst().orElseThrow(() -> new NotFoundException(String.format("Запрос с id = %d не найден", id)));
//
//                if (!participationRequest.getStatus().equals(StatusRequest.PENDING)) {
//                    throw new ConflictException("Запрос невозможно подтвердить.");
//                }
//                if (state.equals(StatusRequest.CONFIRMED)) {
//                    participationRequest.setStatus(StatusRequest.CONFIRMED);
//                    confirmedList.add(participationRequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest)));
//                } else {
//                    participationRequest.setStatus(StatusRequest.REJECTED);
//                    rejectedList.add(participationRequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest)));
//                }
//            }
//        }
//        eventRepository.save(event);
//
//        return new EventRequestStatusUpdateResultDto(confirmedList, rejectedList);
//    }
}
