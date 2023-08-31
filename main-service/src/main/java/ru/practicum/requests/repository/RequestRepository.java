package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.StatusRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEvent_Initiator_IdAndEvent_Id(Long userId, Long eventId);

    List<ParticipationRequest> findAllByEvent_Id(Long eventId);

    ParticipationRequest findByRequester_IdAndEvent_Id(Long userId, Long eventId);

    List<ParticipationRequest> findAllByRequester_Id(Long userId);

    Long countAllByEventIdAndStatus(Long eventId, StatusRequest statusRequest);
}
