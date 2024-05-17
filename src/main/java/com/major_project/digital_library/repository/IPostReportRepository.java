package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.PostReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface IPostReportRepository extends JpaRepository<PostReport, UUID> {
    @Query("SELECT p FROM PostReport p " +
            "WHERE (:status IS NULL OR :status = '' OR p.status = :status) " +
            "AND (:type IS NULL OR :type = '' OR p.type = :type) " +
            "ORDER BY p.reportedAt DESC")
    Page<PostReport> findAllPostReports(String status, String type, Pageable pageable);
}
