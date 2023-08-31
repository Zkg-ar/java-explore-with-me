package ru.practicum.requests.service;

import ru.practicum.events.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.requests.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getParticipationRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto updateParticipationRequest(Long userId, Long eventId, EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto);

    ParticipationRequestDto updateRequestStatus(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByUser(Long userId);
}
