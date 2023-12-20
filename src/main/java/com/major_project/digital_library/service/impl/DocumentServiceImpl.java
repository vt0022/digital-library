package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.repository.IDocumentRepository;
import com.major_project.digital_library.service.IDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements IDocumentService {
    private final IDocumentRepository documentRepository;

    @Autowired
    public DocumentServiceImpl(IDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    @Override
    public <S extends Document> S save(S entity) {
        return documentRepository.save(entity);
    }

    @Override
    public <S extends Document> List<S> saveAll(Iterable<S> entities) {
        return documentRepository.saveAll(entities);
    }

    @Override
    public Optional<Document> findById(UUID uuid) {
        return documentRepository.findById(uuid);
    }

    @Override
    public void deleteById(UUID uuid) {
        documentRepository.deleteById(uuid);
    }

    @Override
    public Page<Document> findAll(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }

    @Override
    public Optional<Document> findBySlug(String slug) {
        return documentRepository.findBySlug(slug);
    }

    @Override
    public Page<Document> findByDocNameContaining(String docName, Pageable pageable) {
        return documentRepository.findByDocNameContaining(docName, pageable);
    }

    @Override
    public Page<Document> findByDocNameAndOrganizationContaining(String docName, Organization organization, Pageable pageable) {
        return documentRepository.findByDocNameAndOrganizationContaining(docName, organization, pageable);
    }

    @Override
    public Page<Document> findAllByOrganization(Organization organization, Pageable pageable) {
        return documentRepository.findAllByOrganization(organization, pageable);
    }

    @Override
    public Page<Document> findByUserUploaded(User user, Pageable pageable) {
        return documentRepository.findByUserUploaded(user, pageable);
    }

    @Override
    public Page<Document> findByUserUploadedAndIsDeleted(User user, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByUserUploadedAndIsDeleted(user, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByVerifiedStatusAndIsDeleted(int VerifiedStatus, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByVerifiedStatusAndIsDeleted(VerifiedStatus, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByOrganizationAndVerifiedStatusAndIsDeleted(Organization organization, int verifiedStatus, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByOrganizationAndVerifiedStatusAndIsDeleted(organization, verifiedStatus, isDeleted, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND d.userUploaded = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    public Page<Document> findByUserUploaded(User user, Organization userOrganization, Pageable pageable) {
        return documentRepository.findByUserUploaded(user, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL)")
    public Page<Document> findAllDocumentsWithFilter(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, Pageable pageable) {
        return documentRepository.findAllDocumentsWithFilter(isDeleted, isInternal, verifiedStatus, category, field, organization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = :userOrganization))")
    public Page<Document> findDocumentsForStudents(Category category, Field field, Organization organization, Organization userOrganization, Pageable pageable) {
        return documentRepository.findDocumentsForStudents(category, field, organization, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false)")
    public Page<Document> findDocumentsForGuests(Category category, Field field, Organization organization, Pageable pageable) {
        return documentRepository.findDocumentsForGuests(category, field, organization, pageable);
    }

    @Override
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
    public Page<Document> searchDocumentsForStudents(Category category, Field field, Organization organization, Organization userOrganization, String query, Pageable pageable) {
        return documentRepository.searchDocumentsForStudents(category, field, organization, userOrganization, query, pageable);
    }

    @Override
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
    public Page<Document> searchDocumentsForGuests(Category category, Field field, Organization organization, String query, Pageable pageable) {
        return documentRepository.searchDocumentsForGuests(category, field, organization, query, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    public Page<Document> searchWithAllDocuments(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, String query, Pageable pageable) {
        return documentRepository.searchWithAllDocuments(isDeleted, isInternal, verifiedStatus, category, field, organization, query, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND MONTH(d.uploadedAt) = MONTH(CURRENT_DATE) AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE)")
    public Page<Document> findLatestDocuments(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, Pageable pageable) {
        return documentRepository.findLatestDocuments(isDeleted, isInternal, verifiedStatus, category, field, organization, pageable);
    }

    @Override
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
    public Page<Document> findUploadedDocuments(Integer verifiedStatus, Category category, Field field, Organization organization, User userUploaded, String query, Pageable pageable) {
        return documentRepository.findUploadedDocuments(verifiedStatus, category, field, organization, userUploaded, query, pageable);
    }

    @Override
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
    public Page<Document> findUploadedDocumentsByUserForStudent(Category category, Field field, Organization userOrganization, User userUploaded, String query, Pageable pageable) {
        return documentRepository.findUploadedDocumentsByUserForStudent(category, field, userOrganization, userUploaded, query, pageable);
    }

    @Override
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
    public Page<Document> findUploadedDocumentsByUserForGuest(Category category, Field field, User userUploaded, String query, Pageable pageable) {
        return documentRepository.findUploadedDocumentsByUserForGuest(category, field, userUploaded, query, pageable);
    }

    @Override
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
    public Page<Document> findLikedDocuments(User user, String query, Pageable pageable) {
        return documentRepository.findLikedDocuments(user, query, pageable);
    }

    @Override
    public long count() {
        return documentRepository.count();
    }

    @Override
    public long countByVerifiedStatus(int verifiedStatus) {
        return documentRepository.countByVerifiedStatus(verifiedStatus);
    }

    @Override
    public long countByOrganization(Organization organization) {
        return documentRepository.countByOrganization(organization);
    }


    @Override
    public long countByVerifiedStatusAndOrganization(int verifiedStatus, Organization organization) {
        return documentRepository.countByVerifiedStatusAndOrganization(verifiedStatus, organization);
    }
}
