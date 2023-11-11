package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IOrganizationService {
    <S extends Organization> S save(S entity);

    Optional<Organization> findById(UUID uuid);

    void deleteById(UUID uuid);

    Page<Organization> findAll(Pageable pageable);

    Optional<Organization> findBySlug(String slug);
}
