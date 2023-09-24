package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleService {
    Optional<Role> findById(UUID uuid);

    <S extends Role> S save(S entity);
}
