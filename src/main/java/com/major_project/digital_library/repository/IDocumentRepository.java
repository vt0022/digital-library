package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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

    // Tìm kiếm tài liệu cho sinh viên kèm bộ lọc
//    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
//            "AND d.verifiedStatus = 1 " +
//            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?1))")
//    Page<Document> findByInternal(Organization userOrganization, Pageable pageable);
//
//    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
//            "AND d.verifiedStatus = 1 " +
//            "AND (d.category = ?1 OR ?1 IS NULL)" +
//            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
//    Page<Document> findByCategory(Category category, Organization userOrganization, Pageable pageable);
//
//    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
//            "AND d.verifiedStatus = 1 " +
//            "AND d.field = ?1 " +
//            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
//    Page<Document> findByField(Field field, Organization userOrganization, Pageable pageable);
//
//    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
//            "AND d.verifiedStatus = 1 " +
//            "AND d.organization = ?1 " +
//            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
//    Page<Document> findByOrganization(Organization organization, Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND d.userUploaded = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    Page<Document> findByUserUploaded(User user, Organization userOrganization, Pageable pageable);

//    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
//            "AND d.verifiedStatus = 1 " +
//            "AND d.category = ?1 " +
//            "AND d.field = ?2 " +
//            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?3))")
//    Page<Document> findByCategoryAndField(Category category, Field field, Organization userOrganization, Pageable pageable);
//
//    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
//            "AND d.verifiedStatus = 1 " +
//            "AND d.category = ?1 " +
//            "AND d.organization = ?2 " +
//            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?3))")
//    Page<Document> findByCategoryAndOrganization(Category category, Organization organization, Organization userOrganization, Pageable pageable);
//
//    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
//            "AND d.verifiedStatus = 1 " +
//            "AND d.field = ?1 " +
//            "AND d.organization = ?2 " +
//            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?3))")
//    Page<Document> findByFieldAndOrganization(Field field, Organization organization, Organization userOrganization, Pageable pageable);

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

//    Page<Document> findByVerifiedStatusAndIsInternalAndIsDeleted(int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByFieldAndVerifiedStatusAndIsInternalAndIsDeleted(Field field, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Organization organization, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByUserUploadedAndVerifiedStatusAndIsInternalAndIsDeleted(User user, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndFieldAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Field field, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Organization organization, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Field field, Organization organization, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Field field, Organization organization, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);


//    Page<Document> findByCategory(Category category, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByField(Field field, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByOrganization(Organization organization, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByUserUploadedAndIsDeleted(User user, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndFieldAndIsDeleted(Category category, Field field, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndOrganizationAndIsDeleted(Category category, Organization organization, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByFieldAndOrganizationAndIsDeleted(Field field, Organization organization, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndFieldAndOrganizationAndIsDeleted(Category category, Field field, Organization organization, boolean isDeleted, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE MONTH(d.uploadedAt) = MONTH(CURRENT_DATE) AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE) ORDER BY d.uploadedAt DESC")
    Page<Document> findLatestDocuments(Pageable pageable);

    @Query("SELECT d FROM Document d " +
            "WHERE MONTH(d.uploadedAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE) " +
            "AND d.organization = ?1 " +
            "ORDER BY d.uploadedAt DESC")
    Page<Document> findLatestDocumentsByOrganization(Organization organization, Pageable pageable);

    long countByVerifiedStatus(int verifiedStatus);

    long countByVerifiedStatusAndOrganization(int verifiedStatus, Organization organization);

    long count();

    long countByOrganization(Organization organization);

    Page<Document> findByCategoryIsDeletedFalse(Pageable pageable);
}
