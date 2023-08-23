package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.HitsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<ViewStatsDto> getAll(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique) {
        if (uri.isEmpty()) {
            return unique ? hitsRepository.getAllUniqueWhereCreatedBetweenStartAndEnd(start, end) :
                    hitsRepository.getAllWhereCreatedBetweenStartAndEnd(start, end);
        } else {
            return unique ? hitsRepository.getAllUniqueWhereCreatedBetweenStartAndEndAndUriInList(start, end, uri) :
                    hitsRepository.getAllWhereCreatedBetweenStartAndEndAndUriInList(start, end, uri);
        }
    }

    //Подлежит удалению
    @Override
    public List<EndpointHitDto> getAllHits() {
        return hitsRepository.findAll().stream().map(endpointHit -> EndpointHitMapper.INSTANCE.toEndpointHitDto(endpointHit)).collect(Collectors.toList());
    }
}
