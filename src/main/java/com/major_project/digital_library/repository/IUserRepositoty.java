package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepositoty extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
}
