package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

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

    @Query("SELECT f FROM Field f " +
            "WHERE (f.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND LOWER(f.fieldName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Field> searchFields(Boolean isDeleted, String query, Pageable pageable);
}
