package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.PostAppeal;
import com.major_project.digital_library.entity.PostReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPostAppealRepository extends JpaRepository<PostAppeal, UUID> {
    @Query("SELECT p FROM PostAppeal p " +
            "WHERE (:status IS NULL OR :status = '' OR p.status = :status) " +
            "AND (:type IS NULL OR :type = '' OR p.type = :type) " +
            "ORDER BY p.appealedAt DESC")
    Page<PostAppeal> findAllPostAppeals(String status, String type, Pageable pageable);

    Optional<PostAppeal> findByPostReport(PostReport postReport);

    long countByAppealedAtBetween(Timestamp startDate, Timestamp endDate);

    @Query("SELECT p.type as type, COUNT(p) as count " +
            "FROM PostAppeal p " +
            "GROUP BY p.type")
    List<Object[]> countPostAppealsByType();

    @Query("SELECT p.type as type, COUNT(p) as count " +
            "FROM PostAppeal p " +
            "WHERE DATE(p.appealedAt) BETWEEN DATE(:startDate) AND DATE(:endDate) " +
            "GROUP BY p.type")
    List<Object[]> countPostAppealsByTypeAndDateRange(Timestamp startDate, Timestamp endDate);
}
