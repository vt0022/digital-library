package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDocumentLikeRepository extends JpaRepository<DocumentLike, UserDocumentKey> {
    boolean existsByUserAndDocument(User user, Document document);

    Optional<DocumentLike> findByUserAndDocument(User user, Document document);

    @Query("SELECT d FROM Document d " +
            "JOIN DocumentLike l " +
            "ON d = l.document " +
            "WHERE l.user = :user " +
            "AND (d.isInternal = false OR d.organization = :organization) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "ORDER BY l.likedAt DESC"
    )
    Page<Document> findLikedDocuments(User user, Organization organization, Pageable pageable);

    @Query("SELECT d FROM Document d " +
            "JOIN DocumentLike l " +
            "ON d = l.document " +
            "WHERE l.user = :user " +
            "AND (d.isInternal = false OR d.organization = :organization) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY l.likedAt DESC"
    )
    Page<Document> searchLikedDocuments(User user, Organization organization, String query, Pageable pageable);
}
