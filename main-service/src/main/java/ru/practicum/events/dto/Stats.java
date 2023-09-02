package ru.practicum.events.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Stats {
    Long eventId;
    Long confirmedRequests;
}