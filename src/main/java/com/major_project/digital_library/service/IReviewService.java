package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.ReviewRequestModel;
import com.major_project.digital_library.model.response_model.ReviewResponseModel;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

public interface IReviewService {

    Page<ReviewResponseModel> getReviewsByDocument(String slug, int rating, int page);

    List<Object[]> countReviewsByStarOfDocument(String slug);

    Page<ReviewResponseModel> getMyReviews(int status, int page, int size);

    ReviewResponseModel approveReview(UUID reviewId, boolean isApproved, String note);

    Page<ReviewResponseModel> getReviewsByOrganization(UUID orgId,
                                                       int verifiedStatus,
                                                       int page,
                                                       int size);

    boolean checkReviewed(String slug);

    ReviewResponseModel reviewDocument(@PathVariable UUID docId, @RequestBody ReviewRequestModel reviewRequestModel);

    ReviewResponseModel editReview(UUID reviewId, ReviewRequestModel reviewRequestModel);

    void deleteReview(UUID reviewId);
}
