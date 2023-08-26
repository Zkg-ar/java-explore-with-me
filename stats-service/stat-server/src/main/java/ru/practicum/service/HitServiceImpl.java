package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.HitsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HitServiceImpl implements HitService {
    private final HitsRepository hitsRepository;

    @Override
    public EndpointHitDto save(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = EndpointHitMapper.INSTANCE.toEndpointHit(endpointHitDto);

        return EndpointHitMapper.INSTANCE.toEndpointHitDto(hitsRepository.save(endpointHit));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getAll(String start, String end, List<String> uri, boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (uri == null) {
            return unique ? hitsRepository.getAllUniqueWhereCreatedBetweenStartAndEnd(startTime, endTime) :
                    hitsRepository.getAllWhereCreatedBetweenStartAndEnd(startTime, endTime);
        } else {
            return unique ? hitsRepository.getAllUniqueWhereCreatedBetweenStartAndEndAndUriInList(startTime, endTime, uri) :
                    hitsRepository.getAllWhereCreatedBetweenStartAndEndAndUriInList(startTime, endTime, uri);
        }
    }

}
