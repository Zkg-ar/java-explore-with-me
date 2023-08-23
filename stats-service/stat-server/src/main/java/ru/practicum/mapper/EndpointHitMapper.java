package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    EndpointHitMapper INSTANCE = Mappers.getMapper(EndpointHitMapper.class);
    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Mapping(target = "timestamp", expression = "java(convertToString(endpointHit.getTimestamp()))")
    EndpointHitDto toEndpointHitDto(EndpointHit endpointHit);

    default LocalDateTime convertToLocalDateTime(String time) {
        return LocalDateTime.parse(time, FORMATTER);
    }

    @Mapping(target = "timestamp", expression = "java(convertToLocalDateTime(endpointHitDto.getTimestamp()))")
    EndpointHit toEndpointHit(EndpointHitDto endpointHitDto);

    default String convertToString(LocalDateTime time) {
        String timestamp = time.format(FORMATTER);
        return timestamp;
    }
}
