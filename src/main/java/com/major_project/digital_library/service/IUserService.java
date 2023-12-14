package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface IUserService {
    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE MONTH(u.createdAt) = MONTH(CURRENT_DATE) AND YEAR(u.createdAt) = YEAR(CURRENT_DATE) ORDER BY u.createdAt DESC")
    Page<User> findLatestUsers(Pageable pageable);

    <S extends User> S save(S entity);

    <S extends User> S update(S entity);

    void deleteById(UUID uuid);

    Optional<User> findById(UUID uuid);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsDeleted(String email, boolean isDeleted);

    Optional<User> findLoggedInUser();

    @Query("SELECT u FROM User u " +
            "WHERE MONTH(u.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(u.createdAt) = YEAR(CURRENT_DATE) " +
            "AND u.organization = :organization " +
            "AND u.role.roleName <> 'ROLE_MANAGER' " +
            "ORDER BY u.createdAt DESC")
    Page<User> findLatestUsersByOrganization(Organization organization, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE u.organization = :organization " +
            "AND u.role.roleName <> 'ROLE_MANAGER'")
    Page<User> findByOrganization(Organization organization, Pageable pageable);

    long countByOrganization(Organization organization);

    long count();
}
