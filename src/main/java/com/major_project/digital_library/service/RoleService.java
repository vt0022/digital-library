package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Role;

public interface RoleService {
    <S extends Role> S save(S entity);
}
