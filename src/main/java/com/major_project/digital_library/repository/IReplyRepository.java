package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
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
public interface IReplyRepository extends JpaRepository<Reply, UUID> {
    Page<Reply> findAllByPostOrderByCreatedAtAsc(Post post, Pageable pageable);

    Page<Reply> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    long countByCreatedAtBetween(Timestamp startDate, Timestamp endDate);

    @Query("SELECT MONTH(r.createdAt) as month, COUNT(r) as count " +
            "FROM Reply r " +
            "WHERE YEAR(r.createdAt) = :year " +
            "GROUP BY MONTH(r.createdAt)")
    List<Object[]> countRepliesByMonth(int year);

    @Query("SELECT r.post.subsection.subName as subsection, COUNT(r) as count " +
            "FROM Reply r " +
            "GROUP BY r.post.subsection")
    List<Object[]> countRepliesBySubsection();

    @Query("SELECT r.post.subsection.subName as subsection, COUNT(r) as count " +
            "FROM Reply r " +
            "WHERE DATE(r.createdAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY r.post.subsection")
    List<Object[]> countRepliesBySubsectionAndDateRange(Timestamp startDate, Timestamp endDate);

    @Query("SELECT r.post.label.labelName, COUNT(r) as count " +
            "FROM Reply r " +
            "GROUP BY r.post.label")
    List<Object[]> countRepliesByLabel();

    @Query("SELECT r.post.label.labelName, COUNT(r) as count " +
            "FROM Reply r " +
            "WHERE DATE(r.createdAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY r.post.label")
    List<Object[]> countRepliesByLabelAndDateRange(Timestamp startDate, Timestamp endDate);

    @Query("SELECT COUNT(r) " +
            "FROM Reply r " +
            "WHERE r.post.label IS NULL")
    long countRepliesWithNoLabel();

    @Query("SELECT COUNT(r) " +
            "FROM Reply r " +
            "WHERE r.post.label IS NULL " +
            "AND DATE(r.createdAt) BETWEEN DATE(:startDate) AND DATE(:endDate)")
    long countRepliesWithNoLabelAndDateRange(Timestamp startDate, Timestamp endDate);
}
