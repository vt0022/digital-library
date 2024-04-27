package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface IReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByDocumentOrganization(Organization organization, Pageable pageable);

    Page<Review> findByVerifiedStatusAndDocumentOrganization(int verifiedStatus, Organization organization, Pageable pageable);

    boolean existsByUserAndDocument(User user, Document document);

    Page<Review> findByDocumentAndVerifiedStatusOrderByCreatedAt(Document document, int verifiedStatus, Pageable pageable);

    Page<Review> findByDocumentAndStarAndVerifiedStatusOrderByCreatedAt(Document document, Integer star, int verifiedStatus, Pageable pageable);

    Page<Review> findByUserOrderByCreatedAt(User user, Pageable pageable);

    Page<Review> findByUserAndVerifiedStatusOrderByCreatedAt(User user, int verifiedStatus, Pageable pageable);

    @Query("SELECT r.star, COUNT(r) FROM Review r " +
            "WHERE r.verifiedStatus = 1 " +
            "AND r.document = :document " +
            "GROUP BY r.star")
    List<Object[]> countReviewsByStarAndDocument(Document document);
}
