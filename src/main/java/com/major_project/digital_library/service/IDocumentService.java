package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDocumentService {
    List<Document> findAll();

    <S extends Document> S save(S entity);

    Optional<Document> findById(UUID uuid);

    void deleteById(UUID uuid);

    Page<Document> findAll(Pageable pageable);

    Optional<Document> findBySlug(String slug);

    Page<Document> findByCategoryIsDeletedFalse(Pageable pageable);

    Page<Document> findByDocNameContaining(String docName, Pageable pageable);

    Page<Document> findByDocNameAndOrganizationContaining(String docName, Organization organization, Pageable pageable);

    Page<Document> findAllByOrganization(Organization organization, Pageable pageable);

    Page<Document> findByUserUploaded(User user, Pageable pageable);

    Page<Document> findByUserUploadedAndIsDeleted(User user, boolean isDeleted, Pageable pageable);

    Page<Document> findByVerifiedStatusAndIsDeleted(int verifiedStatus, boolean isDeleted, Pageable pageable);

    Page<Document> findByOrganizationAndVerifiedStatusAndIsDeleted(Organization organization, int verifiedStatus, boolean isDeleted, Pageable pageable);

//    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
//            "AND d.verifiedStatus = 1 " +
//            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?1))")
//    Page<Document> findByInternal(Organization userOrganization, Pageable pageable);
//
//    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
//            "AND d.verifiedStatus = 1 " +
//            "AND d.category = ?1 " +
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
//
//    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
//            "AND d.verifiedStatus = 1 " +
//            "AND d.category = ?1 " +
//            "AND d.field = ?2 " +
//            "AND d.organization = ?3 " +
//            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?4))")
//    Page<Document> findByCategoryAndFieldAndOrganization(Category category, Field field, Organization organization, Organization userOrganization, Pageable pageable);

//    Page<Document> findByVerifiedStatusAndIsInternalAndIsDeleted(int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByFieldAndVerifiedStatusAndIsInternalAndIsDeleted(Field field, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Organization organization, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByUserUploadedAndVerifiedStatusAndIsInternalAndIsDeleted(User user, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndFieldAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Field field, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Organization organization, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Field field, Organization organization, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Field field, Organization organization, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);

    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL)")
    Page<Document> findAllDocumentsWithFilter(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = :userOrganization))")
    Page<Document> findDocumentsForStudents(Category category, Field field, Organization organization, Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false)")
    Page<Document> findDocumentsForGuests(Category category, Field field, Organization organization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE MONTH(d.uploadedAt) = MONTH(CURRENT_DATE) AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE) ORDER BY d.uploadedAt DESC")
    Page<Document> findLatestDocuments(Pageable pageable);

    long count();

    long countByVerifiedStatus(int verifiedStatus);

    long countByOrganization(Organization organization);

    @Query("SELECT d FROM Document d " +
            "WHERE MONTH(d.uploadedAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE) " +
            "AND d.organization = ?1 " +
            "ORDER BY d.uploadedAt DESC")
    Page<Document> findLatestDocumentsByOrganization(Organization organization, Pageable pageable);

    long countByVerifiedStatusAndOrganization(int verifiedStatus, Organization organization);


//    Page<Document> findByIsDeleted(boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByCategoryAndIsDeleted(Category category, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByFieldAndIsDeleted(Field field, boolean isDeleted, Pageable pageable);
//
//    Page<Document> findByOrganizationAndIsDeleted(Organization organization, boolean isDeleted, Pageable pageable);
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
}
