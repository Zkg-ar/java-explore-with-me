package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents() == null ? Collections.emptyList() : newCompilationDto.getEvents());
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto, events);
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void deleteCompilationById(Long compilationId) {
        Compilation compilation = compilationRepository
                .findById(compilationId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id = %d не найдена", compilationId)));
        compilationRepository.delete(compilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto newCompilationDto) {
        Compilation compilation = compilationRepository
                .findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id = %d не найдена", compId)));

        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(eventRepository.findAllById(newCompilationDto.getEvents()));
        }
        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        if (pinned == null) {
            return compilationRepository
                    .findAll(PageRequest.of(from / size, size))
                    .stream()
                    .map(compilation -> compilationMapper.toCompilationDto(compilation))
                    .collect(Collectors.toList());
        }
        return compilationRepository
                .findAllByPinned(pinned, PageRequest.of(from / size, size))
                .stream()
                .map(compilation -> compilationMapper.toCompilationDto(compilation))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getById(Long compId) {
        Compilation compilation = compilationRepository
                .findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборка с id = %d не найдена", compId)));
        return compilationMapper.toCompilationDto(compilation);
    }
}
