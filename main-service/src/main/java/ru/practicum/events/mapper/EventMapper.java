package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.Constant;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.State;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class, LocationMapper.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "location", source = "location")
    Event toEvent(NewEventDto newEventDto, Location location, Category category,
                  User initiator);


    EventShortDto toEventShortDto(Event event);

    EventFullDto toEventFullDto(Event event);

    default String convertToString(LocalDateTime time) {
        String timestamp = time.format(Constant.FORMATTER);
        return timestamp;
    }

    default LocalDateTime convertToLocalDateTime(String time) {
        return LocalDateTime.parse(time, Constant.FORMATTER);
    }


}