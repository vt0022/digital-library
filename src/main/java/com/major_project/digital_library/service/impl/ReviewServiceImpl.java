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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
