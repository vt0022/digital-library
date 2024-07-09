package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.ReviewRequestModel;
import com.major_project.digital_library.model.response_model.ReviewResponseModel;
import com.major_project.digital_library.repository.IDocumentRepository;
import com.major_project.digital_library.repository.IOrganizationRepository;
import com.major_project.digital_library.repository.IReviewRepository;
import com.major_project.digital_library.service.IReviewService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Transactional
@Service
public class ReviewServiceImpl implements IReviewService {
    private final IReviewRepository reviewRepository;
    private final IDocumentRepository documentRepository;
    private final IOrganizationRepository organizationRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReviewServiceImpl(IReviewRepository reviewRepository, IDocumentRepository documentRepository, IOrganizationRepository organizationRepository, IUserService userService, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.documentRepository = documentRepository;
        this.organizationRepository = organizationRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ReviewResponseModel> getReviewsByDocument(String slug, int rating, int page) {
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page, 4, sort);
        Page<Review> reviews = Page.empty();

        if (rating >= 1 && rating <= 5)
            reviews = reviewRepository.findByDocumentAndStarAndVerifiedStatusOrderByCreatedAt(document, rating, 1, pageable);
        else
            reviews = reviewRepository.findByDocumentAndVerifiedStatusOrderByCreatedAt(document, 1, pageable);

        Page<ReviewResponseModel> reviewResponseModels = reviews.map(this::convertToReviewModel);

        return reviewResponseModels;
    }

    @Override
    public List<Object[]> countReviewsByStarOfDocument(String slug) {
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        List<Object[]> reviewCounts = reviewRepository.countReviewsByStarAndDocument(document);

        Set<Integer> existingStars = new HashSet<>();
        for (Object[] review : reviewCounts) {
            existingStars.add((Integer) review[0]);
        }

        for (int i = 1; i <= 5; i++) {
            if (!existingStars.contains(i)) {
                reviewCounts.add(new Object[]{i, 0});
            }
        }

        return reviewCounts;
    }

    @Override
    public Page<ReviewResponseModel> getMyReviews(int status, int page, int size) {
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Review> reviews = Page.empty();

        if (status >= -1 && status <= 1)
            reviews = reviewRepository.findByUserAndVerifiedStatusOrderByCreatedAtDesc(user, status, pageable);
        else
            reviews = reviewRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        Page<ReviewResponseModel> reviewResponseModels = reviews.map(this::convertToReviewModel);

        return reviewResponseModels;
    }

    @Override
    public ReviewResponseModel approveReview(UUID reviewId, boolean isApproved, String note) {
        User user = userService.findLoggedInUser();

        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));

        review.setVerifiedStatus(isApproved ? 1 : -1);
        if (!isApproved)
            review.setNote(note);
        else
            review.setNote("");
        review.setTimesLeft(review.getTimesLeft() - 1);
        review.setUserVerified(user);
        review.setVerifiedAt(new Timestamp(System.currentTimeMillis()));
        review = reviewRepository.save(review);

        ReviewResponseModel reviewResponseModel = modelMapper.map(review, ReviewResponseModel.class);

        return reviewResponseModel;
    }

    @Override
    public Page<ReviewResponseModel> getReviewsByOrganization(UUID orgId,
                                                              int verifiedStatus,
                                                              int page,
                                                              int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Organization organization = organizationRepository.findById(orgId).orElseThrow(() -> new RuntimeException("Organization not found"));

        Page<Review> reviews = Page.empty();
        if (verifiedStatus >= -1 && verifiedStatus <= 1)
            reviews = reviewRepository.findByVerifiedStatusAndDocumentOrganization(verifiedStatus, organization, pageable);
        else
            reviews = reviewRepository.findByDocumentOrganization(organization, pageable);

        Page<ReviewResponseModel> reviewResponseModels = reviews.map(this::convertToReviewModel);

        return reviewResponseModels;
    }

    @Override
    public boolean checkReviewed(String slug) {
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));
        User user = userService.findLoggedInUser();

        boolean isReviewed = reviewRepository.existsByUserAndDocument(user, document);

        if (isReviewed) return true;
        else return false;
    }

    @Override
    public ReviewResponseModel reviewDocument(@PathVariable UUID docId, @RequestBody ReviewRequestModel reviewRequestModel) {
        Document document = documentRepository.findById(docId).orElseThrow(() -> new RuntimeException("Document not found"));

        User user = userService.findLoggedInUser();

        Review review = modelMapper.map(reviewRequestModel, Review.class);
        review.setDocument(document);
        review.setUser(user);
        review.setVerifiedStatus(0);
        review = reviewRepository.save(review);

        ReviewResponseModel reviewResponseModel = modelMapper.map(review, ReviewResponseModel.class);

        return reviewResponseModel;
    }

    @Override
    public ReviewResponseModel editReview(UUID reviewId, ReviewRequestModel reviewRequestModel) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));

        review.setVerifiedStatus(0);
        review.setStar(reviewRequestModel.getStar());
        review.setContent(reviewRequestModel.getContent());
        review = reviewRepository.save(review);

        ReviewResponseModel reviewResponseModel = modelMapper.map(review, ReviewResponseModel.class);

        return reviewResponseModel;
    }

    @Override
    public void deleteReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));

        reviewRepository.deleteById(reviewId);
    }

    private ReviewResponseModel convertToReviewModel(Review review) {
        ReviewResponseModel reviewResponseModel = modelMapper.map(review, ReviewResponseModel.class);
        return reviewResponseModel;
    }
}
