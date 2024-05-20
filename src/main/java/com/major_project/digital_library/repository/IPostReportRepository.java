package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface IPostReportRepository extends JpaRepository<PostReport, UUID> {
    @Query("SELECT p FROM PostReport p " +
            "WHERE (:status IS NULL OR :status = '' OR p.status = :status) " +
            "AND (:type IS NULL OR :type = '' OR p.type = :type) " +
            "ORDER BY p.reportedAt DESC")
    Page<PostReport> findAllPostReports(String status, String type, Pageable pageable);

    @Query("SELECT p FROM PostReport p " +
            "WHERE p.post = :post " +
            "AND p <> :postReport " +
            "AND p.status IN :status " +
            "ORDER BY p.reportedAt DESC")
    List<PostReport> findAllByPostAndStatus(PostReport postReport, Post post, List<String> status);

    long countByReportedAtBetween(Timestamp startDate, Timestamp endDate);

    @Query("SELECT p.type as type, COUNT(p) as count " +
            "FROM PostReport p " +
            "GROUP BY p.type")
    List<Object[]> countPostReportsByType();

    @Query("SELECT p.type as type, COUNT(p) as count " +
            "FROM PostReport p " +
            "WHERE DATE(p.reportedAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY p.type")
    List<Object[]> countPostReportsByTypeAndDateRange(Timestamp startDate, Timestamp endDate);
}
