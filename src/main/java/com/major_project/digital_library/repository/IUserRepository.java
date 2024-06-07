package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Role;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    long countByCreatedAtBetween(Timestamp startDate, Timestamp endDate);

    long countByOrganization(Organization organization);

    long countByOrganizationAndCreatedAtBetween(Organization organization, Timestamp startDate, Timestamp endDate);

    @Query("SELECT u.organization.orgName as organization, COUNT(u) as count " +
            "FROM User u " +
            "GROUP BY u.organization")
    List<Object[]> countUsersByOrganization();

    @Query("SELECT u.organization.orgName as organization, COUNT(u) as count " +
            "FROM User u " +
            "WHERE DATE(u.createdAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY u.organization")
    List<Object[]> countUsersByOrganizationAndDateRange(Timestamp startDate, Timestamp endDate);

    @Query("SELECT u FROM User u " +
            "WHERE (u.isDisabled = :isDisabled OR :isDisabled IS NULL) " +
            "AND (u.gender = :gender OR :gender IS NULL) " +
            "AND (u.organization = :organization OR :organization IS NULL) " +
            "AND (u.role = :role OR :role IS NULL) " +
            "AND u.role.roleName <> :roleName " +
            "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<User> findUsers(Boolean isDisabled, Integer gender, Organization organization, Role role, String roleName, String query, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE (u.isDisabled = :isDisabled OR :isDisabled IS NULL) " +
            "AND (u.gender = :gender OR :gender IS NULL) " +
            "AND (u.organization = :organization OR :organization IS NULL) " +
            "AND (u.role = :role OR :role IS NULL) " +
            "AND u.role.roleName <> :roleName " +
            "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND MONTH(u.createdAt) = MONTH(CURRENT_DATE) " +
            "AND YEAR(u.createdAt) = YEAR(CURRENT_DATE)")
    Page<User> findLatestUsers(Boolean isDisabled, Integer gender, Organization organization, Role role, String roleName, String query, Pageable pageable);

    @Query("SELECT MONTH(u.createdAt) as month, COUNT(u) as count " +
            "FROM User u " +
            "WHERE (u.organization = :organization OR :organization IS NULL) " +
            "AND YEAR(u.createdAt) = :year " +
            "GROUP BY MONTH(u.createdAt)")
    List<Object[]> countUsersByMonth(int year, Organization organization);

    @Query("SELECT u FROM User u " +
            "WHERE u.isDisabled = FALSE " +
            "AND u.isAuthenticated = TRUE " +
            "AND u.role.roleName = 'ROLE_STUDENT' " +
            "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY " +
            "( " +
            "  ((SELECT COUNT(pa) FROM PostAcceptance pa WHERE pa.post.isDisabled = FALSE AND pa.post.userPosted = u)) * 10 + " +
            "  ((SELECT COUNT(ra) FROM ReplyAcceptance ra WHERE ra.reply.isDisabled = FALSE AND ra.reply.user = u)) * 10 + " +
            "  ((SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.isDisabled = FALSE AND pl.post.userPosted = u)) * 2 + " +
            "  ((SELECT COUNT(rl) FROM ReplyLike rl WHERE rl.reply.isDisabled = FALSE AND rl.reply.user = u)) * 2 " +
            ") DESC, " +
            "(SELECT COUNT(pa) FROM PostAcceptance pa WHERE pa.post.isDisabled = FALSE AND pa.post.userPosted = u ) DESC, " +
            "(SELECT COUNT(ra) FROM ReplyAcceptance ra WHERE ra.reply.isDisabled = FALSE AND ra.reply.user = u) DESC, " +
            "(SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.isDisabled = FALSE AND pl.post.userPosted = u) DESC, " +
            "(SELECT COUNT(rl) FROM ReplyLike rl WHERE rl.reply.isDisabled = FALSE AND rl.reply.user = u) DESC, " +
            "u.createdAt DESC")
    Page<User> findUsersForReputation(String query, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE u.isDisabled = FALSE " +
            "AND u.isAuthenticated = TRUE " +
            "AND u.role.roleName = 'ROLE_STUDENT' " +
            "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY " +
            "( " +
            "  ((SELECT COUNT(pa) FROM PostAcceptance pa WHERE pa.post.isDisabled = FALSE AND pa.post.userPosted = u AND MONTH(pa.acceptedAt) = :month AND YEAR(pa.acceptedAt) = :year)) * 10 + " +
            "  ((SELECT COUNT(ra) FROM ReplyAcceptance ra WHERE ra.reply.isDisabled = FALSE AND ra.reply.user = u AND MONTH(ra.acceptedAt) = :month AND YEAR(ra.acceptedAt) = :year)) * 10 + " +
            "  ((SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.isDisabled = FALSE AND pl.post.userPosted = u AND MONTH(pl.likedAt) = :month AND YEAR(pl.likedAt) = :year)) * 2 + " +
            "  ((SELECT COUNT(rl) FROM ReplyLike rl WHERE rl.reply.isDisabled = FALSE AND rl.reply.user = u AND MONTH(rl.likedAt) = :month AND YEAR(rl.likedAt) = :year)) * 2 " +
            ") DESC, " +
            "(SELECT COUNT(pa) FROM PostAcceptance pa WHERE pa.post.isDisabled = FALSE AND pa.post.userPosted = u  AND MONTH(pa.acceptedAt) = :month AND YEAR(pa.acceptedAt) = :year) DESC, " +
            "(SELECT COUNT(ra) FROM ReplyAcceptance ra WHERE ra.reply.isDisabled = FALSE AND ra.reply.user = u AND MONTH(ra.acceptedAt) = :month AND YEAR(ra.acceptedAt) = :year) DESC, " +
            "(SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.isDisabled = FALSE AND pl.post.userPosted = u AND MONTH(pl.likedAt) = :month AND YEAR(pl.likedAt) = :year) DESC, " +
            "(SELECT COUNT(rl) FROM ReplyLike rl WHERE rl.reply.isDisabled = FALSE AND rl.reply.user = u AND MONTH(rl.likedAt) = :month AND YEAR(rl.likedAt) = :year) DESC, " +
            "u.createdAt DESC")
    Page<User> findUsersForReputationByMonthAndYear(String query, int month, int year, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE u.isDisabled = FALSE " +
            "AND u.isAuthenticated = TRUE " +
            "AND u.role.roleName = 'ROLE_STUDENT' " +
            "AND (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY " +
            "( " +
            "  ((SELECT COUNT(pa) FROM PostAcceptance pa WHERE pa.post.isDisabled = FALSE AND pa.post.userPosted = u AND YEAR(pa.acceptedAt) = :year)) * 10 + " +
            "  ((SELECT COUNT(ra) FROM ReplyAcceptance ra WHERE ra.reply.isDisabled = FALSE AND ra.reply.user = u AND YEAR(ra.acceptedAt) = :year)) * 10 + " +
            "  ((SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.isDisabled = FALSE AND pl.post.userPosted = u AND YEAR(pl.likedAt) = :year)) * 2 + " +
            "  ((SELECT COUNT(rl) FROM ReplyLike rl WHERE rl.reply.isDisabled = FALSE AND rl.reply.user = u AND YEAR(rl.likedAt) = :year)) * 2 " +
            ") DESC, " +
            "(SELECT COUNT(pa) FROM PostAcceptance pa WHERE pa.post.isDisabled = FALSE AND pa.post.userPosted = u AND YEAR(pa.acceptedAt) = :year) DESC, " +
            "(SELECT COUNT(ra) FROM ReplyAcceptance ra WHERE ra.reply.isDisabled = FALSE AND ra.reply.user = u AND YEAR(ra.acceptedAt) = :year) DESC, " +
            "(SELECT COUNT(pl) FROM PostLike pl WHERE pl.post.isDisabled = FALSE AND pl.post.userPosted = u AND YEAR(pl.likedAt) = :year) DESC, " +
            "(SELECT COUNT(rl) FROM ReplyLike rl WHERE rl.reply.isDisabled = FALSE AND rl.reply.user = u AND YEAR(rl.likedAt) = :year) DESC, " +
            "u.createdAt DESC")
    Page<User> findUsersForReputationByYear(String query, int year, Pageable pageable);

    @Query("SELECT u FROM User u " +
            "WHERE u.isDisabled = FALSE " +
            "AND u.isAuthenticated = TRUE " +
            "AND u.role.roleName = 'ROLE_STUDENT'")
    List<User> findActiveStudent();

}
