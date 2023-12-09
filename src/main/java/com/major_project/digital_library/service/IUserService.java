package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    Page<User> findAll(Pageable pageable);

    <S extends User> S save(S entity);

    <S extends User> S update(S entity);

    void deleteById(UUID uuid);

    Optional<User> findById(UUID uuid);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsDeleted(String email, boolean isDeleted);

    Optional<User> findLoggedInUser();
}
