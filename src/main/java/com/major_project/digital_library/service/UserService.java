package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> findByEmail(String email);
}
