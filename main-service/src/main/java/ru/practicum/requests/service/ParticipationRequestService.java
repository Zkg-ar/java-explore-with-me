package ru.practicum.requests.service;

import ru.practicum.events.dto.EventRequestStatusUpdateRequestDto;
import ru.practicum.requests.dto.EventRequestStatusUpdateResultDto;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> getParticipationRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResultDto updateParticipationRequest(Long userId, Long eventId, EventRequestStatusUpdateRequestDto eventRequestStatusUpdateRequestDto);
}
