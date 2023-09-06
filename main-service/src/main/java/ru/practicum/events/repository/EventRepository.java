package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator_Id(Long userId, Pageable page);

    @Query("SELECT e FROM Event e " +
            "WHERE (COALESCE(:userIds, NULL) IS NULL OR e.initiator.id IN :userIds) " +
            "AND (COALESCE(:states, NULL) IS NULL OR e.state IN :states) " +
            "AND (COALESCE(:categoryIds, NULL) IS NULL OR e.category.id IN :categoryIds) " +
            "AND (COALESCE(:rangeStart, NULL) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (COALESCE(:rangeEnd, NULL) IS NULL OR e.eventDate <= :rangeEnd) ")
    List<Event> getEvents(Pageable pageable,
                               @Param("userIds") List<Long> userIds,
                               @Param("states") List<State> states,
                               @Param("categoryIds") List<Long> categoryIds,
                               @Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd
    );

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (COALESCE(:text, NULL) IS NULL OR (LOWER(e.annotation) LIKE LOWER(concat('%', :text, '%')) OR LOWER(e.description) LIKE LOWER(concat('%', :text, '%')))) " +
            "AND (COALESCE(:categoryIds, NULL) IS NULL OR e.category.id IN :categoryIds) " +
            "AND (COALESCE(:paid, NULL) IS NULL OR e.paid = :paid) " +
            "AND (COALESCE(:rangeStart, NULL) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (COALESCE(:rangeEnd, NULL) IS NULL OR e.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = false OR e.id IN " +
            "(SELECT r.event.id " +
            "FROM ParticipationRequest r " +
            "WHERE r.status = 'CONFIRMED' " +
            "GROUP BY r.event.id " +
            "HAVING e.participantLimit - count(id) > 0 " +
            "ORDER BY count(r.id))) ")
    List<Event> searchEvent(@Param("text") String text,
                            @Param("categoryIds") List<Long> categoryIds,
                            @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                            @Param("rangeEnd") LocalDateTime rangeEnd, @Param("onlyAvailable") Boolean onlyAvailable, Pageable pageable);


    Boolean existsEventsByCategory_Name(String name);

    @Query("SELECT MIN(e.publishedOn) FROM Event e WHERE e.id IN :eventsId")
    Optional<LocalDateTime> getMinStart(@Param("eventsId") List<Long> eventsId);
}
