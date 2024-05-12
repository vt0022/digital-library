package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IFieldRepository extends JpaRepository<Field, UUID> {
    Optional<Field> findBySlug(String slug);

    Optional<Field> findByFieldName(String fieldName);

    Page<Field> findByIsDeleted(boolean isDeleted, Pageable pageable);

    @Query("SELECT f FROM Field f " +
            "WHERE (f.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND LOWER(f.fieldName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Field> searchFields(Boolean isDeleted, String query, Pageable pageable);

    long countByCreatedAtBetween(Timestamp startDate, Timestamp endDate);
}
