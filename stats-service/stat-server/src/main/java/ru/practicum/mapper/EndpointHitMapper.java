package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHitMapper INSTANCE = Mappers.getMapper(EndpointHitMapper.class);

    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);
    EndpointHit toEndpointHit(EndpointHitDto userDto);
}
