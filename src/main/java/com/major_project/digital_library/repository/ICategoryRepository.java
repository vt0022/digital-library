package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findBySlug(String slug);

    Optional<Category> findByCategoryName(String categoryName);

    Page<Category> findByIsDeleted(boolean isDeleted, Pageable pageable);

    @Query("SELECT c FROM Category  c " +
            "WHERE (c.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Category> searchCategories(Boolean isDeleted, String query, Pageable pageable);

    long countByCreatedAtBetween(Timestamp startDate, Timestamp endDate);
}
