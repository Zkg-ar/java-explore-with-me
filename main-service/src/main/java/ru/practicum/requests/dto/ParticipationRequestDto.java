package ru.practicum.requests.dto;


import lombok.*;
import ru.practicum.requests.model.StatusRequest;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

    private Long id;

    private Long event;

    private String created;

    private Long requester;

    private StatusRequest status;
}