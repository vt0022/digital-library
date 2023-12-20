package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findBySlug(String slug);

    Optional<Organization> findByOrgName(String orgName);

    Page<Organization> findByIsDeleted(boolean isDeleted, Pageable pageable);

    @Query("SELECT o FROM Organization o " +
            "WHERE (o.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND LOWER(o.orgName) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Organization> searchOrganization(boolean isDeleted, String query, Pageable pageable);
}
