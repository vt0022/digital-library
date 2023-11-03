package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Save;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISaveRepository extends JpaRepository<Save, UUID> {
    boolean existsByUserAndDocument(User user, Document document);

    Optional<Save> findByUserAndDocument(User user, Document document);

    Page<Save> findByUserAndIsSaved(User user, boolean isSaved, Pageable pageable);
}
