package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Subsection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISubsectionRepository extends JpaRepository<Subsection, UUID> {
    Optional<Subsection> findBySlug(String slug);

    Optional<Subsection> findBySubName(String subName);

    Optional<Subsection> findBySlugAndIsDisabled(String slug, boolean isDisabled);

    @Query("SELECT s FROM Subsection s " +
            "WHERE s.isDisabled = FALSE " +
            "AND s.isEditable = TRUE " +
            "AND s.section.isDisabled = FALSE")
    List<Subsection> findEditableSubsections();

    long countByCreatedAtBetween(Timestamp startDate, Timestamp endDate);

    @Query("SELECT s FROM Subsection s " +
            "WHERE (s.isDisabled = :isDisabled OR :isDisabled IS NULL) " +
            "AND (s.isEditable = :isEditable OR :isEditable IS NULL)" +
            "AND LOWER(s.subName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Subsection> searchSubsections(Boolean isDisabled, Boolean isEditable, String query, Pageable pageable);
}
