package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IFieldRepository extends JpaRepository<Field, UUID> {
    Optional<Field> findBySlug(String slug);

    Optional<Field> findByFieldName(String fieldName);

    Page<Field> findByIsDeleted(boolean isDeleted, Pageable pageable);
}
