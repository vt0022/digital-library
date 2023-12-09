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
    public Page<Document> findAllByOrganization(Organization organization, Pageable pageable) {
        return documentRepository.findAllByOrganization(organization, pageable);
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
            "AND d.VerifiedStatus = 1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?1))")
    public Page<Document> findByInternal(Organization userOrganization, Pageable pageable) {
        return documentRepository.findByInternal(userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.category = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    public Page<Document> findByCategory(Category category, Organization userOrganization, Pageable pageable) {
        return documentRepository.findByCategory(category, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.field = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    public Page<Document> findByField(Field field, Organization userOrganization, Pageable pageable) {
        return documentRepository.findByField(field, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.organization = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    public Page<Document> findByOrganization(Organization organization, Organization userOrganization, Pageable pageable) {
        return documentRepository.findByOrganization(organization, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.userUploaded = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    public Page<Document> findByUserUploaded(User user, Organization userOrganization, Pageable pageable) {
        return documentRepository.findByUserUploaded(user, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.category = ?1 " +
            "AND d.field = ?2 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?3))")
    public Page<Document> findByCategoryAndField(Category category, Field field, Organization userOrganization, Pageable pageable) {
        return documentRepository.findByCategoryAndField(category, field, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.category = ?1 " +
            "AND d.organization = ?2 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?3))")
    public Page<Document> findByCategoryAndOrganization(Category category, Organization organization, Organization userOrganization, Pageable pageable) {
        return documentRepository.findByCategoryAndOrganization(category, organization, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.field = ?1 " +
            "AND d.organization = ?2 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?3))")
    public Page<Document> findByFieldAndOrganization(Field field, Organization organization, Organization userOrganization, Pageable pageable) {
        return documentRepository.findByFieldAndOrganization(field, organization, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.VerifiedStatus = 1 " +
            "AND d.category = ?1 " +
            "AND d.field = ?2 " +
            "AND d.organization = ?3 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?4))")
    public Page<Document> findByCategoryAndFieldAndOrganization(Category category, Field field, Organization organization, Organization userOrganization, Pageable pageable) {
        return documentRepository.findByCategoryAndFieldAndOrganization(category, field, organization, userOrganization, pageable);
    }

    @Override
    public Page<Document> findByVerifiedStatusAndIsInternalAndIsDeleted(int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByVerifiedStatusAndIsInternalAndIsDeleted(VerifiedStatus, isInternal, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByCategoryAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByCategoryAndVerifiedStatusAndIsInternalAndIsDeleted(category, VerifiedStatus, isInternal, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByFieldAndVerifiedStatusAndIsInternalAndIsDeleted(Field field, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByFieldAndVerifiedStatusAndIsInternalAndIsDeleted(field, VerifiedStatus, isInternal, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Organization organization, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(organization, VerifiedStatus, isInternal, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByUserUploadedAndVerifiedStatusAndIsInternalAndIsDeleted(User user, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByUserUploadedAndVerifiedStatusAndIsInternalAndIsDeleted(user, VerifiedStatus, isInternal, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByCategoryAndFieldAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Field field, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByCategoryAndFieldAndVerifiedStatusAndIsInternalAndIsDeleted(category, field, VerifiedStatus, isInternal, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByCategoryAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Organization organization, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByCategoryAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(category, organization, VerifiedStatus, isInternal, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Field field, Organization organization, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(field, organization, VerifiedStatus, isInternal, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByCategoryAndFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(Category category, Field field, Organization organization, int VerifiedStatus, boolean isInternal, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByCategoryAndFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(category, field, organization, VerifiedStatus, isInternal, isDeleted, pageable);
    }
}
