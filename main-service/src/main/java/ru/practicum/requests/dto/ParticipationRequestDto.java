package ru.practicum.requests.dto;


import lombok.*;
import ru.practicum.requests.model.StatusRequest;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ParticipationRequestDto {

    private Long id;

    private Long event;

    private LocalDateTime created;

    private Long requester;

    private StatusRequest status;
}