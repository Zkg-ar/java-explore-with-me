package ru.practicum.service;

import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    EndpointHitDto save(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getAll(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique);

}
