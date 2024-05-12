package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Label;
import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Subsection;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface IPostRepository extends JpaRepository<Post, UUID> {
    @Query("SELECT p FROM Post p " +
            "WHERE (p.subsection = :subsection OR :subsection IS NULL) " +
            "AND (p.label = :label OR :label IS NULL) " +
            "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) ")
    Page<Post> findAllPosts(
            Subsection subsection,
            Label label,
            String query,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE (p.subsection = :subsection OR :subsection IS NULL) " +
            "AND (p.label = :label OR :label IS NULL) " +
            "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY SIZE(p.postLikes) DESC")
    Page<Post> findAllPostsOrderByTotalLikesDesc(
            Subsection subsection,
            Label label,
            String query,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE (p.subsection = :subsection OR :subsection IS NULL) " +
            "AND (p.label = :label OR :label IS NULL) " +
            "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY SIZE(p.postLikes) ASC")
    Page<Post> findAllPostsOrderByTotalLikesAsc(
            Subsection subsection,
            Label label,
            String query,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE (p.subsection = :subsection OR :subsection IS NULL) " +
            "AND (p.label = :label OR :label IS NULL) " +
            "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY SIZE(p.replies) DESC")
    Page<Post> findAllPostsOrderByTotalRepliesDesc(
            Subsection subsection,
            Label label,
            String query,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE (p.subsection = :subsection OR :subsection IS NULL) " +
            "AND (p.label = :label OR :label IS NULL) " +
            "AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY SIZE(p.replies) ASC")
    Page<Post> findAllPostsOrderByTotalRepliesAsc(
            Subsection subsection,
            Label label,
            String query,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND p.userPosted = :user " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findAllByUser(User user, String query, Pageable pageable);

    @Query("SELECT p " +
            "FROM Post p " +
            "JOIN p.tags t " +
            "WHERE t.tagName IN :tags " +
            "GROUP BY p.postId " +
            "ORDER BY COUNT(t) DESC")
    Page<Post> findAllByTags(List<String> tags, Pageable pageable);

    long countByCreatedAtBetween(Timestamp startDate, Timestamp endDate);

    @Query("SELECT MONTH(p.createdAt) as month, COUNT(p) as count " +
            "FROM Post p " +
            "WHERE YEAR(p.createdAt) = :year " +
            "GROUP BY MONTH(p.createdAt)")
    List<Object[]> countPostsByMonth(int year);

    @Query("SELECT p.subsection.subName as subsection, COUNT(p) as count " +
            "FROM Post p " +
            "GROUP BY p.subsection")
    List<Object[]> countPostsBySubsection();

    @Query("SELECT p.subsection.subName as subsection, COUNT(p) as count " +
            "FROM Post p " +
            "WHERE DATE(p.createdAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY p.subsection")
    List<Object[]> countPostsBySubsectionAndDateRange(Timestamp startDate, Timestamp endDate);

    @Query("SELECT p.label.labelName, COUNT(p) as count " +
            "FROM Post p " +
            "GROUP BY p.label")
    List<Object[]> countPostsByLabel();

    @Query("SELECT p.label.labelName, COUNT(p) as count " +
            "FROM Post p " +
            "WHERE DATE(p.createdAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY p.label")
    List<Object[]> countPostsByLabelAndDateRange(Timestamp startDate, Timestamp endDate);

    @Query("SELECT COUNT(p)" +
            "FROM Post p " +
            "WHERE p.label IS NULL")
    long countPostsWithNoLabel();

    @Query("SELECT COUNT(p)" +
            "FROM Post p " +
            "WHERE p.label IS NULL " +
            "AND DATE(p.createdAt) BETWEEN DATE(:startDate) AND DATE(:endDate)")
    long countPostsWithNoLabelAndDateRange(Timestamp startDate, Timestamp endDate);
}
