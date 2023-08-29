package ru.practicum.events.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator_Id(Long userId, Pageable page);

    List<Event> findAllByInitiator_IdInAndStateInAndAndCategory_IdInAndCreatedOnBetween(List<Long> users, List<String> states,
                                                                                         List<Long> categories, LocalDateTime rangeStart,
                                                                                         LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT event FROM Event event WHERE LOWER(event.annotation) LIKE  LOWER(CONCAT('%', ?1, '%'))" +
            "OR LOWER(event.description) LIKE LOWER(CONCAT('%', ?1, '%')) " +
            "AND event.category.id IN ?2 " +
            "AND event.paid = ?3 " +
            "AND event.state IN ?4 ")
    List<Event> searchEvent(String text, List<Long> categories, Boolean paid, State state, Pageable pageable);


}
