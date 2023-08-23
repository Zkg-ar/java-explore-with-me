package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.HitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping
public class StatsController {

    private final HitService hitService;

    @PostMapping("/hit")
    public EndpointHitDto saveInfo(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Сохранение информации о том, что к эндпоинту был запрос");
        return hitService.save(endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam("end") @DateTimeFormat (pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime end,
                                       @RequestParam(required = false) List<String> uri,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Получение статистики по посещениям.");
        return hitService.getAll(start, end, uri, unique);
    }

}
