package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.repository.IOrganizationRepository;
import com.major_project.digital_library.service.IOrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationServiceImpl implements IOrganizationService {
    private final IOrganizationRepository organizationRepository;

    @Autowired
    public OrganizationServiceImpl(IOrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public <S extends Organization> S save(S entity) {
        return organizationRepository.save(entity);
    }

    @Override
    public Optional<Organization> findById(UUID uuid) {
        return organizationRepository.findById(uuid);
    }

    @Override
    public Optional<Organization> findByOrgName(String orgName) {
        return organizationRepository.findByOrgName(orgName);
    }

    @Override
    public void deleteById(UUID uuid) {
        organizationRepository.deleteById(uuid);
    }

    @Override
    public Page<Organization> findAll(Pageable pageable) {
        return organizationRepository.findAll(pageable);
    }

    @Override
    public Optional<Organization> findBySlug(String slug) {
        return organizationRepository.findBySlug(slug);
    }

    @Override
    public Page<Organization> findByIsDeleted(boolean isDeleted, Pageable pageable) {
        return organizationRepository.findByIsDeleted(isDeleted, pageable);
    }

    @Override
    @Query("SELECT o FROM Organization o " +
            "WHERE (o.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND LOWER(o.orgName) LIKE LOWER(CONCAT('%', :query, '%'))")
    public Page<Organization> searchOrganization(boolean isDeleted, String query, Pageable pageable) {
        return organizationRepository.searchOrganization(isDeleted, query, pageable);
    }
}
