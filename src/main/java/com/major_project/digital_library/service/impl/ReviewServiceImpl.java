package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.repository.IReviewRepository;
import com.major_project.digital_library.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
    public void deleteById(UUID uuid) {
        reviewRepository.deleteById(uuid);
    }
}
