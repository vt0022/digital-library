package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IFieldService {
    <S extends Field> S save(S entity);

    Optional<Field> findById(UUID uuid);

    Optional<Field> findByFieldName(String fieldName);

    void deleteById(UUID uuid);

    Page<Field> findAll(Pageable pageable);

    Optional<Field> findBySlug(String slug);

    Page<Field> findByIsDeleted(boolean isDeleted, Pageable pageable);
}
