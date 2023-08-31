package ru.practicum.compilation.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UpdateCompilationDto {
    private List<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    String title;
}
