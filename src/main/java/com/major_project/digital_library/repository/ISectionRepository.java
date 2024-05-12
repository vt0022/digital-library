package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISectionRepository extends JpaRepository<Section, UUID> {
    Optional<Section> findBySectionName(String sectionName);

    List<Section> findAllByIsDisabled(boolean isDisabled);

    @Query("SELECT s FROM Section s " +
            "WHERE (s.isDisabled = :isDisabled OR :isDisabled IS NULL) " +
            "AND LOWER(s.sectionName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Section> searchSections(Boolean isDisabled, String query, Pageable pageable);
}
