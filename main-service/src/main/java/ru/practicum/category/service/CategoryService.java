package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long catId, CategoryDto newCategoryDto);

    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long catId);

    void deleteCategory(Long catId);
}
