package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitsRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh WHERE eh.timestamp BETWEEN ?1 AND ?2 GROUP BY eh.uri, eh.app ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> getAllUniqueWhereCreatedBetweenStartAndEnd(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(eh.ip)) " +
            "FROM EndpointHit eh WHERE eh.timestamp BETWEEN ?1 AND ?2 GROUP BY eh.uri, eh.app ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> getAllWhereCreatedBetweenStartAndEnd(LocalDateTime start, LocalDateTime end);

    @Query("SELECT  new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) " +
            "FROM EndpointHit eh WHERE eh.timestamp BETWEEN ?1 AND ?2 AND eh.uri IN ?3 GROUP BY eh.uri, eh.app ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> getAllUniqueWhereCreatedBetweenStartAndEndAndUriInList(LocalDateTime start, LocalDateTime end, List<String> uri);

    @Query("SELECT  new ru.practicum.model.ViewStats(eh.app, eh.uri, COUNT(eh.ip)) " +
            "FROM EndpointHit eh " +
            "WHERE eh.timestamp BETWEEN ?1 AND ?2 " +
            "AND eh.uri IN ?3 GROUP BY eh.uri, eh.app ORDER BY COUNT(eh.ip) DESC")
    List<ViewStats> getAllWhereCreatedBetweenStartAndEndAndUriInList(LocalDateTime start, LocalDateTime end, List<String> uri);

}
