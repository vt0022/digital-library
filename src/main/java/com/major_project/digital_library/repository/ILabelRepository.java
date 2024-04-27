package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Label;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ILabelRepository extends JpaRepository<Label, UUID> {
    Optional<Label> findBySlug(String slug);

    Page<Label> findAllByIsDisabled(boolean isDisabled, Pageable pageable);
}
