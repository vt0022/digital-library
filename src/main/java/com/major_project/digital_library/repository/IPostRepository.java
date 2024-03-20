package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IPostRepository extends JpaRepository<Post, UUID> {
    @Query("SELECT p FROM Post p " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY p.createdAt")
    Page<Document> findAllPostsOrderByCreatedAt(
            String query,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY SIZE(p.postLikes) DESC")
    Page<Document> findAllPostsOrderByTotalLikesDesc(
            String query,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY SIZE(p.postLikes) ASC")
    Page<Document> findAllPostsOrderByTotalLikesAsc(
            String query,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY SIZE(p.replies) DESC")
    Page<Document> findAllPostsOrderByTotalRepliesDesc(
            String query,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY SIZE(p.replies) ASC")
    Page<Document> findAllPostsOrderByTotalRepliesAsc(
            String query,
            Pageable pageable
    );

    @Query("SELECT p FROM Post p " +
            "WHERE (LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "ORDER BY p.totalViews")
    Page<Document> findAllPostsOrderByTotalViews(
            String query,
            Pageable pageable
    );
}
