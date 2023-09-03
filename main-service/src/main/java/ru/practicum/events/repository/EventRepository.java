package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator_Id(Long userId, Pageable page);

    @Query("SELECT event FROM Event event " +
            "WHERE event.initiator.id IN ?1 " +
            "AND event.state IN ?2 " +
            "AND event.category.id IN ?3 " +
            "AND event.eventDate BETWEEN ?4 AND ?5")
    List<Event> getEvents(List<Long> users, List<State> states,
                          List<Long> categories, LocalDateTime rangeStart,
                          LocalDateTime rangeEnd, Pageable pageable);


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

//    @Query("SELECT event FROM Event event " +
//            "WHERE " +
//            "LOWER(event.annotation) LIKE LOWER(CONCAT('%', ?1, '%'))" +
//            "OR LOWER(event.description) LIKE LOWER(CONCAT('%', ?1, '%')) " +
//            "AND event.category.id IN ?2 " +
//            "AND event.paid = ?3 " +
//            "AND event.state ='PUBLISHED' " +
//            "AND event.eventDate BETWEEN ?4 AND ?5 ")
//    List<Event> searchEvent(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);


    Boolean existsEventsByCategory_Name(String name);
}
