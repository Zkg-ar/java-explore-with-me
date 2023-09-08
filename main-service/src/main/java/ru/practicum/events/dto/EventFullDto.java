package ru.practicum.events.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.Constant;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.events.model.State;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    private Long confirmedRequests;
    @JsonFormat(pattern = Constant.FORMAT)
    private LocalDateTime createdOn;

    private String description;
    @JsonFormat(pattern = Constant.FORMAT)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;
    @JsonFormat(pattern = Constant.FORMAT)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private State state;

    private String title;

    private Long views;
    private List<CommentDto> comments;

}