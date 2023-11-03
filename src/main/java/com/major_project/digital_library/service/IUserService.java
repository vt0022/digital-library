package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    <S extends User> S save(S entity);

    Optional<User> findById(UUID uuid);

    Optional<User> findByEmail(String email);
}
