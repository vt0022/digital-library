package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Label;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ILabelRepository extends JpaRepository<Label, UUID> {
    Optional<Label> findBySlug(String slug);

    Optional<Label> findByLabelName(String labelName);

    Page<Label> findAllByIsDisabled(boolean isDisabled, Pageable pageable);

    long countByCreatedAtBetween(Timestamp startDate, Timestamp endDate);

    @Query("SELECT l FROM Label l " +
            "WHERE (l.isDisabled = :isDisabled OR :isDisabled IS NULL) " +
            "AND LOWER(l.labelName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Label> searchLabels(Boolean isDisabled, String query, Pageable pageable);
}
