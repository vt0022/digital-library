package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Role;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepositoty extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsDeleted(String email, boolean isDeleted);

    @Query("SELECT u FROM User u WHERE MONTH(u.createdAt) = MONTH(CURRENT_DATE) AND YEAR(u.createdAt) = YEAR(CURRENT_DATE) ORDER BY u.createdAt DESC")
    Page<User> findLatestUsers(Pageable pageable);

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

    long count();

    long countByOrganization(Organization organization);

    @Query("SELECT u FROM User u " +
            "WHERE (u.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (u.gender = :gender OR :gender IS NULL) " +
            "AND (u.organization = :organization OR :organization IS NULL) " +
            "AND (u.role = :role OR :role IS NULL) " +
            "AND u.role.roleName <> :roleName " +
            "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<User> searchUsers(Boolean isDeleted, Integer gender, Organization organization, Role role, String roleName, String query, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (u.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (u.gender = :gender OR :gender IS NULL) " +
            "AND (u.organization = :organization OR :organization IS NULL) " +
            "AND (u.role = :role OR :role IS NULL) " +
            "AND u.role.roleName <> :roleName " +
            "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND MONTH(u.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(u.createdAt) = YEAR(CURRENT_DATE)")
    Page<User> searchLatestUsers(Boolean isDeleted, Integer gender, Organization organization, Role role, String roleName, String query, Pageable pageable);
}
