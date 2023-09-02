package ru.practicum.category.service;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        Category category = categoryMapper.toCategory(newCategoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найдена", catId)));
        if (eventRepository.existsEventsByCategory_Name(category.getName())) {
            throw new ConflictException("Категория с таким именем уже существует");
        }
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto updateCategory(Long catId, NewCategoryDto categoryDto) {
        Category category = categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найдена", catId)));
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new ConflictException("Категория с таким именем уже существует");
        }

        Category newCategory = categoryMapper.toCategory(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(newCategory));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAll(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size))
                .stream()
                .map(category -> categoryMapper.toCategoryDto(category))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(Long catId) {
        Category category = categoryRepository
                .findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с id = %d не найдена", catId)));
        return categoryMapper.toCategoryDto(category);
    }
}
