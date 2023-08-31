package ru.practicum.compilation.dto;

import lombok.Data;
import ru.practicum.events.dto.EventFullDto;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CompilationDto {
    private List<EventFullDto> events;
    @NotNull
    private Long id;
    @NotNull
    private Boolean pinned;
    @NotNull
    private String title;

}
