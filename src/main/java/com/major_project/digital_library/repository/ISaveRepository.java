package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Save;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ISaveRepository extends JpaRepository<Save, UUID> {
    boolean existsByUserAndDocument(User user, Document document);

    Optional<Save> findByUserAndDocument(User user, Document document);

    @Query("SELECT d FROM Document d " +
            "JOIN Save s " +
            "ON d = s.document " +
            "WHERE s.user = :user " +
            "AND (d.isInternal = false OR d.organization = :organization) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "ORDER BY s.savedAt DESC"
    )
    Page<Document> findSavedDocuments(User user, Organization organization, Pageable pageable);

    @Query("SELECT d FROM Document d " +
            "JOIN Save s " +
            "ON d = s.document " +
            "WHERE s.user = :user " +
            "AND (d.isInternal = false OR d.organization = :organization) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY s.savedAt DESC"
    )
    Page<Document> searchSavedDocuments(User user, Organization organization, String query, Pageable pageable);
}
