package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilations(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Добавление новой подборки (подборка может не содержать событий)");
        return compilationService.createCompilation(compilationDto);
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long compId) {
        log.info("Удаление подборки");
        compilationService.deleteCompilationById(compId);
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto updateCompilations(@PathVariable Long compId, @Valid @RequestBody UpdateCompilationDto compilationDto) {
        log.info("Обновить информацию о подборке");
        return compilationService.updateCompilation(compId, compilationDto);
    }


}
