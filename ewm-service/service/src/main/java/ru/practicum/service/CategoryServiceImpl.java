package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.entity.Category;
import ru.practicum.exception.IdNotFoundException;
import ru.practicum.model.dto.CategoryDto;
import ru.practicum.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.utils.CustomPageRequest.pageRequestOf;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CategoryDto save(CategoryDto category) {
        Category categoryEntity = categoryRepository.save(modelMapper.map(category, Category.class));
        log.debug("Категория сохранена");
        return modelMapper.map(categoryEntity, CategoryDto.class);
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDto category) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new IdNotFoundException("Категория с id = " + catId + " не найдена"));
        category.setId(catId);
        Category categoryEntity = categoryRepository.save(modelMapper.map(category, Category.class));
        log.debug("категория обновлена");
        return modelMapper.map(categoryEntity, CategoryDto.class);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new IdNotFoundException("Категоря с id = " + catId + " не найдена"));
        log.debug("Категория удалена");
        categoryRepository.deleteById(catId);
    }

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        log.debug("получены все категории");
        return categoryRepository.findAll(pageRequestOf(from, size)).stream()
                .map(it -> modelMapper.map(it, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new IdNotFoundException("Категория с id = " + catId + " не найдена"));
        log.debug("Получена категория по id");
        return modelMapper.map(category, CategoryDto.class);
    }
}
