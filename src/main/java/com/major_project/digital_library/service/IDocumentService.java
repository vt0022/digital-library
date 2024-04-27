package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.request_model.DocumentRequestModel;
import com.major_project.digital_library.model.response_model.DetailDocumentResponseModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IDocumentService {
    List<Document> findAll();

    <S extends Document> S save(S entity);

    <S extends Document> List<S> saveAll(Iterable<S> entities);

    Optional<Document> findById(UUID uuid);

    void deleteById(UUID uuid);

    Page<Document> findAll(Pageable pageable);

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
    Page<Document> searchDocumentsForStudents(Category category, Field field, Organization organization, Organization userOrganization, String query, Pageable pageable);

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
    Page<Document> searchDocumentsForGuests(Category category, Field field, Organization organization, String query, Pageable pageable);

    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Document> searchWithAllDocuments(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, String query, Pageable pageable);

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
    Page<Document> searchLatestDocuments(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, String query, Pageable pageable);

    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND MONTH(d.uploadedAt) = MONTH(CURRENT_DATE) AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE)")
    Page<Document> findLatestDocuments(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, Pageable pageable);

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
    Page<Document> findUploadedDocuments(Integer verifiedStatus, Category category, Field field, Organization organization, User userUploaded, String query, Pageable pageable);

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
    Page<Document> findUploadedDocumentsByUserForStudent(Category category, Field field, Organization userOrganization, User userUploaded, String query, Pageable pageable);

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
    Page<Document> findUploadedDocumentsByUserForGuest(Category category, Field field, User userUploaded, String query, Pageable pageable);

    @Query("SELECT s.document FROM Save s " +
            "JOIN s.document d " +
            "WHERE s.user = :user " +
            "AND s.isSaved = true " +
            "AND (d.isInternal = false OR d.organization = s.user.organization) " +
            "AND d.isDeleted = false " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Document> findLikedDocuments(User user, String query, Pageable pageable);

    long count();

    long countByVerifiedStatus(int verifiedStatus);

    long countByOrganization(Organization organization);

    long countByVerifiedStatusAndOrganization(int verifiedStatus, Organization organization);

    @Query("SELECT MONTH(d.uploadedAt) as month, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(d.uploadedAt)")
    List<Object[]> countDocumentsByMonth(Organization organization);

    @Query("SELECT d.category.categoryName as category, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "GROUP BY d.category")
    List<Object[]> countDocumentsByCategory(Organization organization);

    @Query("SELECT d.field.fieldName as category, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "GROUP BY d.field")
    List<Object[]> countDocumentsByField(Organization organization);

    @Query("SELECT d.organization.orgName as organization, COUNT(d) as count " +
            "FROM Document d " +
            "GROUP BY d.organization")
    List<Object[]> countDocumentsByOrganization();

    DetailDocumentResponseModel viewDocument(String slug);

    DetailDocumentResponseModel viewDocumentForGuest(String slug);

    Page<DocumentResponseModel> getAllDocuments(int page, int size, String order, String category, String field, String organization, String deleted, String internal, String status, String s);

    Page<DocumentResponseModel> getMyUploads(int page, int size, String order, String category, String organization, String field, String status, String s);

    Page<DocumentResponseModel> getOwnedDocuments(int page, int size);

    Page<DocumentResponseModel> getDocumentsByUser(UUID userId, int page, int size);

    Page<DocumentResponseModel> findDocumentsByUserForStudent(UUID userId, int page, int size, String order, String category, String field, String s);

    Page<DocumentResponseModel> findDocumentsByUserForGuest(UUID userId, int page, int size, String order, String category, String field, String s);

    Page<DocumentResponseModel> getDocumentsForGuests(int page, int size, String order, String sortOrder, String category, String field, String organization, String s);

    Page<DocumentResponseModel> getPendingDocuments(int page, int size);

    Page<DocumentResponseModel> getDocumentsForStudent(int page, int size, String order, String sortOrder, String category, String field, String organization, String s);

    DocumentResponseModel uploadDocument(DocumentRequestModel documentRequestModel,
                                         MultipartFile multipartFile);

    DocumentResponseModel updateDocument(String slug, DocumentRequestModel documentRequestModel,
                                         MultipartFile multipartFile);

    void deleteDocument(UUID docId);

    void approveDocument(UUID docId, boolean isApproved, String note);

    Page<DocumentResponseModel> getLatestDocuments(int page, int size, String order, String category, String field, String organization, String deleted, String internal, String status, String s);

    Page<DocumentResponseModel> findRelatedDocuments(String query);
}
