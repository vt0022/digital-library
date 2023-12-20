package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IOrganizationService {
    <S extends Organization> S save(S entity);

    Optional<Organization> findById(UUID uuid);

    Optional<Organization> findByOrgName(String orgName);

    void deleteById(UUID uuid);

    Page<Organization> findAll(Pageable pageable);

    Optional<Organization> findBySlug(String slug);

    Page<Organization> findByIsDeleted(boolean isDeleted, Pageable pageable);

    @Query("SELECT o FROM Organization o " +
            "WHERE (o.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND LOWER(o.orgName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Organization> searchOrganization(boolean isDeleted, String query, Pageable pageable);
}
