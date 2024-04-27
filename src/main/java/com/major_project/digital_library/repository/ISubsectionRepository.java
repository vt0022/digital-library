package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Subsection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISubsectionRepository extends JpaRepository<Subsection, UUID> {
    Optional<Subsection> findBySlug(String slug);

    @Query("SELECT s FROM Subsection s " +
            "WHERE s.isDisabled = FALSE " +
            "AND s.isEditable = TRUE " +
            "AND s.section.isDisabled = FALSE")
    List<Subsection> findEditableSubsections();
}
