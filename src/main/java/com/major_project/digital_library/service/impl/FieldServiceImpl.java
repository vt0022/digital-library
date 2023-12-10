package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Field;
import com.major_project.digital_library.repository.IFieldRepository;
import com.major_project.digital_library.service.IFieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FieldServiceImpl implements IFieldService {
    private final IFieldRepository fieldRepository;

    @Autowired
    public FieldServiceImpl(IFieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    @Override
    public <S extends Field> S save(S entity) {
        return fieldRepository.save(entity);
    }

    @Override
    public Optional<Field> findById(UUID uuid) {
        return fieldRepository.findById(uuid);
    }

    @Override
    public Optional<Field> findByFieldName(String fieldName) {
        return fieldRepository.findByFieldName(fieldName);
    }

    @Override
    public void deleteById(UUID uuid) {
        fieldRepository.deleteById(uuid);
    }

    @Override
    public Page<Field> findAll(Pageable pageable) {
        return fieldRepository.findAll(pageable);
    }

    @Override
    public Optional<Field> findBySlug(String slug) {
        return fieldRepository.findBySlug(slug);
    }

    @Override
    public Page<Field> findByIsDeleted(boolean isDeleted, Pageable pageable) {
        return fieldRepository.findByIsDeleted(isDeleted, pageable);
    }
}
