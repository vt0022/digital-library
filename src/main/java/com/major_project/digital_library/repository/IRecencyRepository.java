package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Recency;
import com.major_project.digital_library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IRecencyRepository extends JpaRepository<Recency, UUID> {
    Optional<Recency> findByUserAndDocument(User user, Document document);

    @Query("SELECT d FROM Document d " +
            "JOIN Recency r " +
            "ON d = r.document " +
            "WHERE r.user = :user " +
            "AND (d.isInternal = false OR d.organization = :organization) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "ORDER BY r.accessedAt DESC"
    )
    List<Document> findRecentDocuments(User user, Organization organization);

}
