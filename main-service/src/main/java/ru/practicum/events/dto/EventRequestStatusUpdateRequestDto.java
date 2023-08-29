package ru.practicum.events.dto;


import lombok.*;
import ru.practicum.requests.model.StatusRequest;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequestDto {

    private List<Long> requestIds;
    private StatusRequest status;
}