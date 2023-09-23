package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.repository.OrganizationRepository;
import com.major_project.digital_library.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;

    @Autowired
    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
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
    public void deleteById(UUID uuid) {
        organizationRepository.deleteById(uuid);
    }

    @Override
    public Page<Organization> findAll(Pageable pageable) {
        return organizationRepository.findAll(pageable);
    }
}
