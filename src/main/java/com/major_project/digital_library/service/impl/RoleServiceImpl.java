package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Role;
import com.major_project.digital_library.repository.RoleRepository;
import com.major_project.digital_library.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<Role> findById(UUID uuid) {
        return roleRepository.findById(uuid);
    }

    @Override
    public <S extends Role> S save(S entity) {
        return roleRepository.save(entity);
    }
}
