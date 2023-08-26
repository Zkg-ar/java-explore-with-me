package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.ViewStats;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {
    ViewStatsDto toViewStatsDto(ViewStats viewStats);

    ViewStats toViewStats(ViewStatsDto viewStatsDto);
}
