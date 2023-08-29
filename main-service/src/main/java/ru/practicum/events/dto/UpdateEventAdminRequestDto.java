package ru.practicum.events.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.Constant;
import ru.practicum.location.dto.LocationDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequestDto {
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;
    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = Constant.FORMAT)
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;
    @Size(min = 3, max = 120)
    private String title;
}