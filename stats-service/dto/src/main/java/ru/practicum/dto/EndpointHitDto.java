package ru.practicum.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EndpointHitDto {

    private Long id;
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @NotBlank
    private String timestamp;
}
