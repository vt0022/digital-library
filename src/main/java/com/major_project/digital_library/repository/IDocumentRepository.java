package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.*;
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
public interface IDocumentRepository extends JpaRepository<Document, UUID> {
    Optional<Document> findBySlug(String slug);

    Page<Document> findByDocNameContaining(String docName, Pageable pageable);

    Page<Document> findByDocNameAndOrganizationContaining(String docName, Organization organization, Pageable pageable);

    Page<Document> findAllByOrganization(Organization organization, Pageable pageable);

    Page<Document> findByUserUploaded(User user, Pageable pageable);

    Page<Document> findByUserUploadedAndIsDeleted(User user, boolean isDeleted, Pageable pageable);

    Page<Document> findByVerifiedStatusAndIsDeleted(int verifiedStatus, boolean isDeleted, Pageable pageable);

    Page<Document> findByOrganizationAndVerifiedStatusAndIsDeleted(Organization organization, int verifiedStatus, boolean isDeleted, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND d.userUploaded = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    Page<Document> findByUserUploaded(User user, Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL)")
    Page<Document> findAllDocumentsWithFilter(
            Boolean isDeleted,
            Boolean isInternal,
            Integer verifiedStatus,
            Category category,
            Field field,
            Organization organization,
            Pageable pageable
    );

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = :userOrganization))")
    Page<Document> findDocumentsForStudents(
            Category category,
            Field field,
            Organization organization,
            Organization userOrganization,
            Pageable pageable
    );

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false)")
    Page<Document> findDocumentsForGuests(
            Category category,
            Field field,
            Organization organization,
            Pageable pageable
    );

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = :userOrganization)) " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Document> searchDocumentsForStudents(
            Category category,
            Field field,
            Organization organization,
            Organization userOrganization,
            String query,
            Pageable pageable
    );

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false) " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Document> searchDocumentsForGuests(
            Category category,
            Field field,
            Organization organization,
            String query,
            Pageable pageable
    );

    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Document> searchWithAllDocuments(
            Boolean isDeleted,
            Boolean isInternal,
            Integer verifiedStatus,
            Category category,
            Field field,
            Organization organization,
            String query,
            Pageable pageable
    );

    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND MONTH(d.uploadedAt) = MONTH(CURRENT_DATE) AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE)")
    Page<Document> searchLatestDocuments(
            Boolean isDeleted,
            Boolean isInternal,
            Integer verifiedStatus,
            Category category,
            Field field,
            Organization organization,
            String query,
            Pageable pageable
    );

    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND MONTH(d.uploadedAt) = MONTH(CURRENT_DATE) AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE)")
    Page<Document> findLatestDocuments(
            Boolean isDeleted,
            Boolean isInternal,
            Integer verifiedStatus,
            Category category,
            Field field,
            Organization organization,
            Pageable pageable
    );

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND d.userUploaded = :userUploaded " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Document> findUploadedDocuments(
            Integer verifiedStatus,
            Category category,
            Field field,
            Organization organization,
            User userUploaded,
            String query,
            Pageable pageable
    );

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND (d.verifiedStatus = 1) " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = :userOrganization)) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND d.userUploaded = :userUploaded " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Document> findUploadedDocumentsByUserForStudent(
            Category category,
            Field field,
            Organization userOrganization,
            User userUploaded,
            String query,
            Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND (d.verifiedStatus = 1) " +
            "AND (d.isInternal = false) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND d.userUploaded = :userUploaded " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Document> findUploadedDocumentsByUserForGuest(
            Category category,
            Field field,
            User userUploaded,
            String query,
            Pageable pageable);

    long countByVerifiedStatus(int verifiedStatus);

    long countByVerifiedStatusAndUploadedAtBetween(int verifiedStatus, Timestamp startDate, Timestamp endDate);

    long countByVerifiedStatusAndOrganization(int verifiedStatus, Organization organization);

    long countByVerifiedStatusAndOrganizationAndUploadedAtBetween(int verifiedStatus, Organization organization, Timestamp startDate, Timestamp endDate);

    long countByUploadedAtBetween(Timestamp startDate, Timestamp endDate);

    long countByOrganization(Organization organization);

    long countByOrganizationAndUploadedAtBetween(Organization organization, Timestamp startDate, Timestamp endDate);

    @Query("SELECT MONTH(d.uploadedAt) as month, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "AND YEAR(d.uploadedAt) = :year " +
            "GROUP BY MONTH(d.uploadedAt)")
    List<Object[]> countDocumentsByMonth(int year, Organization organization);

    @Query("SELECT d.category.categoryName as category, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "GROUP BY d.category")
    List<Object[]> countDocumentsByCategory(Organization organization);

    @Query("SELECT d.category.categoryName as category, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "AND DATE(d.uploadedAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY d.category")
    List<Object[]> countDocumentsByCategoryAndDateRange(Timestamp startDate, Timestamp endDate, Organization organization);

    @Query("SELECT d.field.fieldName as category, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "GROUP BY d.field")
    List<Object[]> countDocumentsByField(Organization organization);

    @Query("SELECT d.field.fieldName as category, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "AND DATE(d.uploadedAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY d.field")
    List<Object[]> countDocumentsByFieldAndDateRange(Timestamp startDate, Timestamp endDate, Organization organization);

    @Query("SELECT d.organization.orgName as organization, COUNT(d) as count " +
            "FROM Document d " +
            "GROUP BY d.organization")
    List<Object[]> countDocumentsByOrganization();

    @Query("SELECT d.organization.orgName as organization, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE DATE(d.uploadedAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY d.organization")
    List<Object[]> countDocumentsByOrganizationAndDateRange(Timestamp startDate, Timestamp endDate);

    @Query("SELECT d FROM Document d JOIN d.tags t " +
            "WHERE t IN :tags " +
            "AND d <> :document " +
            "GROUP BY d.docId " +
            "ORDER BY COUNT(t) DESC")
    Page<Document> findRelatedDocumentsByTags(Document document, List<Tag> tags, Pageable pageable);

    @Query("SELECT d FROM Document d " +
            "JOIN d.collectionDocuments c " +
            "WHERE c.collection = :collection " +
            "AND d.isInternal = false " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "ORDER BY c.addedAt DESC"
    )
    Page<Document> findByCollectionForGuest(Collection collection, Pageable pageable);

    @Query("SELECT d FROM Document d " +
            "JOIN d.collectionDocuments c " +
            "WHERE c.collection = :collection " +
            "AND (d.isInternal = false OR d.organization = :organization) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "ORDER BY c.addedAt DESC"
    )
    Page<Document> findByCollectionForUser(Collection collection, Organization organization, Pageable pageable);
}
