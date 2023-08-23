package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    EndpointHitDto save(EndpointHitDto endpointHitDto);
    List<EndpointHitDto> getAllHits();

    List<ViewStatsDto> getAll(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique);

}
