package ru.practicum.service;

import ru.practicum.model.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryDto category);

    CategoryDto update(Long catId, CategoryDto category);

    void delete(Long catId);

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto findById(Long catId);
}
