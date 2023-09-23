package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Field;
import com.major_project.digital_library.repository.FieldRepository;
import com.major_project.digital_library.service.FieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FieldServiceImpl implements FieldService {
    private final FieldRepository fieldRepository;

    @Autowired
    public FieldServiceImpl(FieldRepository fieldRepository) {
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
    public void deleteById(UUID uuid) {
        fieldRepository.deleteById(uuid);
    }

    @Override
    public Page<Field> findAll(Pageable pageable) {
        return fieldRepository.findAll(pageable);
    }
}
