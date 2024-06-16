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

    @Query("SELECT u.organization.orgName AS organization, COUNT(u) AS count " +
            "FROM User u " +
            "GROUP BY u.organization")
    List<Object[]> countUsersByOrganization();

    @Query("SELECT u.organization.orgName AS organization, COUNT(u) AS count " +
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

    @Query("SELECT MONTH(u.createdAt) AS month, COUNT(u) AS count " +
            "FROM User u " +
            "WHERE (u.organization = :organization OR :organization IS NULL) " +
            "AND YEAR(u.createdAt) = :year " +
            "GROUP BY MONTH(u.createdAt)")
    List<Object[]> countUsersByMonth(int year, Organization organization);

    @Query(value = "SELECT BIN_TO_UUID(u.user_id) userId, u.first_name firstName, u.last_name lastName, u.image, u.email, u.created_at createdAt, " +
            "COALESCE(pa.totalPostAcceptances, 0) totalPostAcceptances, COALESCE(ra.totalReplyAcceptances, 0) totalReplyAcceptances, COALESCE(pl.totalPostLikes, 0) totalPostLikes, COALESCE(rl.totalReplyLikes, 0) totalReplyLikes, " +
            "(COALESCE(pa.totalPostAcceptances, 0) * 10 + COALESCE(ra.totalReplyAcceptances, 0) * 10 + COALESCE(pl.totalPostLikes, 0) * 2 + COALESCE(rl.totalReplyLikes, 0) * 2) totalScores " +
            "FROM User u " +
            "LEFT JOIN (SELECT Post.posted_by, COUNT(*) totalPostAcceptances FROM Post JOIN Post_Acceptance ON Post_Acceptance.post_id = Post.post_id WHERE Post.is_disabled = FALSE GROUP BY Post.posted_by) pa ON u.user_id = pa.posted_by " +
            "LEFT JOIN (SELECT Reply.replied_by, COUNT(*) totalReplyAcceptances FROM Reply JOIN Reply_Acceptance ON Reply_Acceptance.reply_id = Reply.reply_id WHERE Reply.is_disabled = FALSE GROUP BY Reply.replied_by) ra ON u.user_id = ra.replied_by " +
            "LEFT JOIN (SELECT Post.posted_by, COUNT(*) totalPostLikes FROM Post JOIN Post_Like ON Post_Like.post_id = Post.post_id WHERE Post.is_disabled = FALSE GROUP BY Post.posted_by) pl ON u.user_id = pl.posted_by " +
            "LEFT JOIN (SELECT Reply.replied_by, COUNT(*) totalReplyLikes FROM Reply JOIN Reply_Like ON Reply_Like.reply_id = Reply.reply_id WHERE Reply.is_disabled = FALSE GROUP BY Reply.replied_by) rl ON u.user_id = rl.replied_by " +
            "WHERE u.is_disabled = FALSE " +
            "AND u.is_authenticated = TRUE " +
            "AND (LOWER(u.first_name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "GROUP BY u.user_id " +
            "ORDER BY " +
            "(COALESCE(pa.totalPostAcceptances, 0) * 10 + " +
            "COALESCE(ra.totalReplyAcceptances, 0) * 10 + " +
            "COALESCE(pl.totalPostLikes, 0) * 2 + " +
            "COALESCE(rl.totalReplyLikes, 0) * 2) DESC, " +
            "COALESCE(pa.totalPostAcceptances, 0) DESC, " +
            "COALESCE(ra.totalReplyAcceptances, 0) DESC, " +
            "COALESCE(pl.totalPostLikes, 0) DESC, " +
            "COALESCE(rl.totalReplyLikes, 0) DESC, " +
            "u.created_at",
            countQuery = "SELECT COUNT(*) FROM User " +
                    "WHERE is_disabled = FALSE " +
                    "AND is_authenticated = TRUE " +
                    "AND (LOWER(first_name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                    "OR LOWER(last_name) LIKE LOWER(CONCAT('%', :query, '%')))",
            nativeQuery = true)
    Page<Object[]> findUsersForReputation(String query, Pageable pageable);

    @Query(value = "SELECT BIN_TO_UUID(u.user_id) userId, u.first_name firstName, u.last_name lastName, u.image, u.email, u.created_at createdAt, " +
            "COALESCE(pa.totalPostAcceptances, 0) totalPostAcceptances, COALESCE(ra.totalReplyAcceptances, 0) totalReplyAcceptances, COALESCE(pl.totalPostLikes, 0) totalPostLikes, COALESCE(rl.totalReplyLikes, 0) totalReplyLikes, " +
            "(COALESCE(pa.totalPostAcceptances, 0) * 10 + COALESCE(ra.totalReplyAcceptances, 0) * 10 + COALESCE(pl.totalPostLikes, 0) * 2 + COALESCE(rl.totalReplyLikes, 0) * 2) totalScores " +
            "FROM User u " +
            "LEFT JOIN (SELECT Post.posted_by, COUNT(*) totalPostAcceptances FROM Post JOIN Post_Acceptance ON Post_Acceptance.post_id = Post.post_id WHERE Post.is_disabled = FALSE AND MONTH(Post_Acceptance.accepted_at) = :month AND YEAR(Post_Acceptance.accepted_at) = :year GROUP BY Post.posted_by) pa ON u.user_id = pa.posted_by " +
            "LEFT JOIN (SELECT Reply.replied_by, COUNT(*) totalReplyAcceptances FROM Reply JOIN Reply_Acceptance ON Reply_Acceptance.reply_id = Reply.reply_id WHERE Reply.is_disabled = FALSE AND MONTH(Reply_Acceptance.accepted_at) = :month AND YEAR(Reply_Acceptance.accepted_at) = :year GROUP BY Reply.replied_by) ra ON u.user_id = ra.replied_by " +
            "LEFT JOIN (SELECT Post.posted_by, COUNT(*) totalPostLikes FROM Post JOIN Post_Like ON Post_Like.post_id = Post.post_id WHERE Post.is_disabled = FALSE AND MONTH(Post_Like.liked_at) = :month AND YEAR(Post_Like.liked_at) = :year GROUP BY Post.posted_by) pl ON u.user_id = pl.posted_by " +
            "LEFT JOIN (SELECT Reply.replied_by, COUNT(*) totalReplyLikes FROM Reply JOIN Reply_Like ON Reply_Like.reply_id = Reply.reply_id WHERE Reply.is_disabled = FALSE AND MONTH(Reply_Like.liked_at) = :month AND YEAR(Reply_Like.liked_at) = :year GROUP BY Reply.replied_by) rl ON u.user_id = rl.replied_by " +
            "WHERE u.is_disabled = FALSE " +
            "AND u.is_authenticated = TRUE " +
            "AND (LOWER(u.first_name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "GROUP BY u.user_id " +
            "ORDER BY " +
            "(COALESCE(pa.totalPostAcceptances, 0) * 10 + " +
            "COALESCE(ra.totalReplyAcceptances, 0) * 10 + " +
            "COALESCE(pl.totalPostLikes, 0) * 2 + " +
            "COALESCE(rl.totalReplyLikes, 0) * 2) DESC, " +
            "COALESCE(pa.totalPostAcceptances, 0) DESC, " +
            "COALESCE(ra.totalReplyAcceptances, 0) DESC, " +
            "COALESCE(pl.totalPostLikes, 0) DESC, " +
            "COALESCE(rl.totalReplyLikes, 0) DESC, " +
            "u.created_at",
            countQuery = "SELECT COUNT(*) FROM User " +
                    "WHERE is_disabled = FALSE " +
                    "AND is_authenticated = TRUE " +
                    "AND (LOWER(first_name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                    "OR LOWER(last_name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
                    "AND :month = :month " +
                    "AND :year = :year",
            nativeQuery = true)
    Page<Object[]> findUsersForReputationByMonthAndYear(String query, int month, int year, Pageable pageable);

    @Query(value = "SELECT BIN_TO_UUID(u.user_id) userId, u.first_name firstName, u.last_name lastName, u.image, u.email, u.created_at createdAt, " +
            "COALESCE(pa.totalPostAcceptances, 0) totalPostAcceptances, COALESCE(ra.totalReplyAcceptances, 0) totalReplyAcceptances, COALESCE(pl.totalPostLikes, 0) totalPostLikes, COALESCE(rl.totalReplyLikes, 0) totalReplyLikes, " +
            "(COALESCE(pa.totalPostAcceptances, 0) * 10 + COALESCE(ra.totalReplyAcceptances, 0) * 10 + COALESCE(pl.totalPostLikes, 0) * 2 + COALESCE(rl.totalReplyLikes, 0) * 2) totalScores " +
            "FROM User u " +
            "LEFT JOIN (SELECT Post.posted_by, COUNT(*) totalPostAcceptances FROM Post JOIN Post_Acceptance ON Post_Acceptance.post_id = Post.post_id WHERE Post.is_disabled = FALSE AND YEAR(Post_Acceptance.accepted_at) = :year GROUP BY Post.posted_by) pa ON u.user_id = pa.posted_by " +
            "LEFT JOIN (SELECT Reply.replied_by, COUNT(*) totalReplyAcceptances FROM Reply JOIN Reply_Acceptance ON Reply_Acceptance.reply_id = Reply.reply_id WHERE Reply.is_disabled = FALSE AND YEAR(Reply_Acceptance.accepted_at) = :year GROUP BY Reply.replied_by) ra ON u.user_id = ra.replied_by " +
            "LEFT JOIN (SELECT Post.posted_by, COUNT(*) totalPostLikes FROM Post JOIN Post_Like ON Post_Like.post_id = Post.post_id WHERE Post.is_disabled = FALSE AND YEAR(Post_Like.liked_at) = :year GROUP BY Post.posted_by) pl ON u.user_id = pl.posted_by " +
            "LEFT JOIN (SELECT Reply.replied_by, COUNT(*) totalReplyLikes FROM Reply JOIN Reply_Like ON Reply_Like.reply_id = Reply.reply_id WHERE Reply.is_disabled = FALSE AND YEAR(Reply_Like.liked_at) = :year GROUP BY Reply.replied_by) rl ON u.user_id = rl.replied_by " +
            "WHERE u.is_disabled = FALSE " +
            "AND u.is_authenticated = TRUE " +
            "AND (LOWER(u.first_name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "GROUP BY u.user_id " +
            "ORDER BY " +
            "(COALESCE(pa.totalPostAcceptances, 0) * 10 + " +
            "COALESCE(ra.totalReplyAcceptances, 0) * 10 + " +
            "COALESCE(pl.totalPostLikes, 0) * 2 + " +
            "COALESCE(rl.totalReplyLikes, 0) * 2) DESC, " +
            "COALESCE(pa.totalPostAcceptances, 0) DESC, " +
            "COALESCE(ra.totalReplyAcceptances, 0) DESC, " +
            "COALESCE(pl.totalPostLikes, 0) DESC, " +
            "COALESCE(rl.totalReplyLikes, 0) DESC, " +
            "u.created_at",
            countQuery = "SELECT COUNT(*) FROM User " +
                    "WHERE is_disabled = FALSE " +
                    "AND is_authenticated = TRUE " +
                    "AND (LOWER(first_name) LIKE LOWER(CONCAT('%', :query, '%')) " +
                    "OR LOWER(last_name) LIKE LOWER(CONCAT('%', :query, '%'))) " +
                    "AND :year = :year",
            nativeQuery = true)
    Page<Object[]> findUsersForReputationByYear(String query, int year, Pageable pageable);
}
