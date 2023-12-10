package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Category;
import com.major_project.digital_library.repository.ICategoryRepository;
import com.major_project.digital_library.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryServiceImpl implements ICategoryService {
    private final ICategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public <S extends Category> S save(S entity) {
        return categoryRepository.save(entity);
    }

    @Override
    public Optional<Category> findByCategoryName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }

    @Override
    public Optional<Category> findBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    @Override
    public Optional<Category> findById(UUID uuid) {
        return categoryRepository.findById(uuid);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public Page<Category> findByIsDeleted(boolean isDeleted, Pageable pageable) {
        return categoryRepository.findByIsDeleted(isDeleted, pageable);
    }

    @Override
    public void deleteById(UUID uuid) {
        categoryRepository.deleteById(uuid);
    }
}
