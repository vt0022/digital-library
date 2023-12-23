package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ICategoryService {
    <S extends Category> S save(S entity);

    Optional<Category> findByCategoryName(String categoryName);

    Optional<Category> findBySlug(String slug);

    Optional<Category> findById(UUID uuid);

    Page<Category> findAll(Pageable pageable);

    Page<Category> findByIsDeleted(boolean isDeleted, Pageable pageable);

    void deleteById(UUID uuid);

    @Query("SELECT c FROM Category  c " +
            "WHERE (c.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Category> searchCategories(Boolean isDeleted, String query, Pageable pageable);
}
