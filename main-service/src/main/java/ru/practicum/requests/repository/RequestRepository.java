package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.events.dto.Stats;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.StatusRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByEvent_Initiator_IdAndEvent_Id(Long userId, Long eventId);

    List<ParticipationRequest> findAllByEvent_Id(Long eventId);

    ParticipationRequest findByRequester_IdAndEvent_Id(Long userId, Long eventId);

    List<ParticipationRequest> findAllByRequester_Id(Long userId);

    Long countParticipationByEventIdAndStatus(Long id, StatusRequest status);

    @Query("SELECT new ru.practicum.events.dto.Stats(pr.event.id, count(pr.id)) " +
            "FROM ParticipationRequest AS pr " +
            "WHERE pr.event.id IN ?1 " +
            "AND pr.status = 'CONFIRMED' " +
            "GROUP BY pr.event.id")
    List<Stats> findConfirmedRequests(List<Long> eventsId);

    Long countAllByEventIdAndStatus(Long eventId, StatusRequest statusRequest);
}
