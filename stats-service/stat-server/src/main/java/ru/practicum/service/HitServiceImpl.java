package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.BadRequest;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.HitsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HitServiceImpl implements HitService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final HitsRepository hitsRepository;
    private final EndpointHitMapper endpointHitMapper;
    private final ViewStatsMapper viewStatsMapper;

    @Override
    public EndpointHitDto save(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = endpointHitMapper.toEndpointHit(endpointHitDto);

        return endpointHitMapper.toEndpointHitDto(hitsRepository.save(endpointHit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getAll(String start, String end, List<String> uri, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);
        if (endTime.isBefore(startTime)) {
            throw new BadRequest("Время окончания не может быть позже времени начала");
        }
        if (uri == null) {
            return unique ? hitsRepository.getAllUniqueWhereCreatedBetweenStartAndEnd(startTime, endTime)
                    .stream()
                    .map(viewStats -> viewStatsMapper.toViewStatsDto(viewStats))
                    .collect(Collectors.toList()) :
                    hitsRepository.getAllWhereCreatedBetweenStartAndEnd(startTime, endTime)
                            .stream()
                            .map(viewStats -> viewStatsMapper.toViewStatsDto(viewStats))
                            .collect(Collectors.toList());
        } else {
            return unique ? hitsRepository.getAllUniqueWhereCreatedBetweenStartAndEndAndUriInList(startTime, endTime, uri)
                    .stream()
                    .map(viewStats -> viewStatsMapper.toViewStatsDto(viewStats))
                    .collect(Collectors.toList()) :
                    hitsRepository.getAllWhereCreatedBetweenStartAndEndAndUriInList(startTime, endTime, uri)
                            .stream()
                            .map(viewStats -> viewStatsMapper.toViewStatsDto(viewStats))
                            .collect(Collectors.toList());
        }
    }

}
