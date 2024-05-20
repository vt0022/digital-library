package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.ReplyAppeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface IReplyAppealRepository extends JpaRepository<ReplyAppeal, UUID> {
    @Query("SELECT r FROM ReplyAppeal r " +
            "WHERE (:status IS NULL OR :status = '' OR r.status = :status) " +
            "AND (:type IS NULL OR :type = '' OR r.type = :type) " +
            "ORDER BY r.appealedAt DESC")
    Page<ReplyAppeal> findAllReplyAppeals(String status, String type, Pageable pageable);

    long countByAppealedAtBetween(Timestamp startDate, Timestamp endDate);

    @Query("SELECT r.type as type, COUNT(r) as count " +
            "FROM ReplyAppeal r " +
            "GROUP BY r.type")
    List<Object[]> countReplyAppealsByType();

    @Query("SELECT r.type as type, COUNT(r) as count " +
            "FROM ReplyAppeal r " +
            "WHERE DATE(r.appealedAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY r.type")
    List<Object[]> countReplyAppealsByTypeAndDateRange(Timestamp startDate, Timestamp endDate);
}
