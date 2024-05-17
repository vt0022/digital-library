package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.ReplyReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface IReplyReportRepository extends JpaRepository<ReplyReport, UUID> {
    @Query("SELECT r FROM ReplyReport r " +
            "WHERE (:status IS NULL OR :status = '' OR r.status = :status) " +
            "AND (:type IS NULL OR :type = '' OR r.type = :type) " +
            "ORDER BY r.reportedAt DESC")
    Page<ReplyReport> findAllReplyReports(String status, String type, Pageable pageable);
}
