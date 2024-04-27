package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IReviewRepository;
import com.major_project.digital_library.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class ReviewServiceImpl implements IReviewService {
    private final IReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(IReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public <S extends Review> S save(S entity) {
        return reviewRepository.save(entity);
    }

    @Override
    public Page<Review> findByDocumentOrganization(Organization organization, Pageable pageable) {
        return reviewRepository.findByDocumentOrganization(organization, pageable);
    }

    @Override
    public Page<Review> findByVerifiedStatusAndDocumentOrganization(int verifiedStatus, Organization organization, Pageable pageable) {
        return reviewRepository.findByVerifiedStatusAndDocumentOrganization(verifiedStatus, organization, pageable);
    }

    @Override
    public Optional<Review> findById(UUID uuid) {
        return reviewRepository.findById(uuid);
    }

    @Override
    public void deleteById(UUID uuid) {
        reviewRepository.deleteById(uuid);
    }

    @Override
    public boolean existsByUserAndDocument(User user, Document document) {
        return reviewRepository.existsByUserAndDocument(user, document);
    }

    @Override
    public Page<Review> findByDocumentAndVerifiedStatusOrderByCreatedAt(Document document, int verifiedStatus, Pageable pageable) {
        return reviewRepository.findByDocumentAndVerifiedStatusOrderByCreatedAt(document, verifiedStatus, pageable);
    }

    @Override
    public Page<Review> findByDocumentAndStarAndVerifiedStatusOrderByCreatedAt(Document document, Integer star, int verifiedStatus, Pageable pageable) {
        return reviewRepository.findByDocumentAndStarAndVerifiedStatusOrderByCreatedAt(document, star, verifiedStatus, pageable);
    }

    @Override
    public Page<Review> findByUserOrderByCreatedAt(User user, Pageable pageable) {
        return reviewRepository.findByUserOrderByCreatedAt(user, pageable);
    }

    @Override
    public Page<Review> findByUserAndVerifiedStatusOrderByCreatedAt(User user, int verifiedStatus, Pageable pageable) {
        return reviewRepository.findByUserAndVerifiedStatusOrderByCreatedAt(user, verifiedStatus, pageable);
    }

    @Override
    @Query("SELECT r.star, COUNT(r) FROM Review r " +
            "WHERE r.verifiedStatus = 1 " +
            "AND r.document = :document " +
            "GROUP BY r.star")
    public List<Object[]> countReviewsByStarAndDocument(Document document) {
        return reviewRepository.countReviewsByStarAndDocument(document);
    }
}
