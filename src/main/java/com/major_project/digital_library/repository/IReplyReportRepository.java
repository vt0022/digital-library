package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface IReplyReportRepository extends JpaRepository<ReplyReport, UUID> {
    @Query("SELECT r FROM ReplyReport r " +
            "WHERE (:status IS NULL OR :status = '' OR r.status = :status) " +
            "AND (:type IS NULL OR :type = '' OR r.type = :type) " +
            "ORDER BY r.reportedAt DESC")
    Page<ReplyReport> findAllReplyReports(String status, String type, Pageable pageable);

    @Query("SELECT r FROM ReplyReport r " +
            "WHERE r.reply = :reply " +
            "AND r <> :replyReport " +
            "AND r.status IN :status " +
            "ORDER BY r.reportedAt DESC")
    List<ReplyReport> findAllByReplyAndStatus(ReplyReport replyReport, Reply reply, List<String> status);

    long countByReportedAtBetween(Timestamp startDate, Timestamp endDate);

    @Query("SELECT r.type as type, COUNT(r) as count " +
            "FROM ReplyReport r " +
            "GROUP BY r.type")
    List<Object[]> countReplyReportsByType();

    @Query("SELECT r.type as type, COUNT(r) as count " +
            "FROM ReplyReport r " +
            "WHERE DATE(r.reportedAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY r.type")
    List<Object[]> countReplyReportsByTypeAndDateRange(Timestamp startDate, Timestamp endDate);
}
