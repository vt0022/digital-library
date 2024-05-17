package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.ReplyAppeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface IReplyAppealRepository extends JpaRepository<ReplyAppeal, UUID> {
    @Query("SELECT r FROM ReplyAppeal r " +
            "WHERE (:status IS NULL OR :status = '' OR r.status = :status) " +
            "AND (:type IS NULL OR :type = '' OR r.type = :type) " +
            "ORDER BY r.appealedAt DESC")
    Page<ReplyAppeal> findAllReplyAppeals(String status, String type, Pageable pageable);
}
