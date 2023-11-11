package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IDocumentService {
    <S extends Document> S save(S entity);

    Optional<Document> findById(UUID uuid);

    void deleteById(UUID uuid);

    Page<Document> findAll(Pageable pageable);

    Optional<Document> findBySlug(String slug);

    Page<Document> findAllByOrganization(Organization organization, Pageable pageable);

    Page<Document> findByUserUploadedAndIsDeleted(User user, boolean isDeleted, Pageable pageable);

    Page<Document> findByVerifiedStatusAndIsDeleted(int verifiedStatus, boolean isDeleted, Pageable pageable);

    Page<Document> findByOrganizationAndVerifiedStatusAndIsDeleted(Organization organization, int verifiedStatus, boolean isDeleted, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?1))")
    Page<Document> findByInternal(Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.category = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    Page<Document> findByCategory(Category category, Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.field = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    Page<Document> findByField(Field field, Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.organization = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    Page<Document> findByOrganization(Organization organization, Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.userUploaded = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    Page<Document> findByUserUploaded(User user, Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.category = ?1 " +
            "AND d.field = ?2 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?3))")
    Page<Document> findByCategoryAndField(Category category, Field field, Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.category = ?1 " +
            "AND d.organization = ?2 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?3))")
    Page<Document> findByCategoryAndOrganization(Category category, Organization organization, Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.field = ?1 " +
            "AND d.organization = ?2 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?3))")
    Page<Document> findByFieldAndOrganization(Field field, Organization organization, Organization userOrganization, Pageable pageable);

    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.category = ?1 " +
            "AND d.field = ?2 " +
            "AND d.organization = ?3 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?4))")
    Page<Document> findByCategoryAndFieldAndOrganization(Category category, Field field, Organization organization, Organization userOrganization, Pageable pageable);

    Page<Document> findByVerifiedStatusAndIsInternalAndIsDeleted(int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);

    Page<Document> findByCategoryAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);

    Page<Document> findByFieldAndVerifiedStatusAndIsInternalAndIsDeleted(Field field, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);

    Page<Document> findByOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Organization organization, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);

    Page<Document> findByUserUploadedAndVerifiedStatusAndIsInternalAndIsDeleted(User user, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);

    Page<Document> findByCategoryAndFieldAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Field field, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);

    Page<Document> findByCategoryAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Organization organization, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);

    Page<Document> findByFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Field field, Organization organization, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);

    Page<Document> findByCategoryAndFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Field field, Organization organization, int verifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable);


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
