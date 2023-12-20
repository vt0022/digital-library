package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.ReviewRequestModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.ReviewResponseModel;
import com.major_project.digital_library.service.IDocumentService;
import com.major_project.digital_library.service.IOrganizationService;
import com.major_project.digital_library.service.IReviewService;
import com.major_project.digital_library.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {
    private final IReviewService reviewService;
    private final IDocumentService documentService;
    private final IOrganizationService organizationService;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReviewController(IReviewService reviewService, IDocumentService documentService, IOrganizationService organizationService, IUserService userService, ModelMapper modelMapper) {
        this.reviewService = reviewService;
        this.documentService = documentService;
        this.organizationService = organizationService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Lấy đánh giá của một tài liệu",
            description = "Trả về tất cả đánh giá của một tài liệu")
    @GetMapping("/documents/{slug}/reviews")
    public ResponseEntity<?> getReviewsByDocument(@PathVariable String slug) {
        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        List<ReviewResponseModel> reviewResponseModels = document.getReviews()
                .stream()
                .map(reviewResponse -> modelMapper.map(reviewResponse, ReviewResponseModel.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get reviews of documents successfully")
                .data(reviewResponseModels)
                .build());
    }

    @Operation(summary = "Lấy đánh giá của một trường",
            description = "Trả về tất cả đánh giá của một trường")
    @GetMapping("/organizations/{orgId}/reviews")
    public ResponseEntity<?> getReviewsByOrganization(@PathVariable UUID orgId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "15") int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Organization organization = organizationService.findById(orgId).orElseThrow(() -> new RuntimeException("Organization not found"));

        Page<Review> reviews = reviewService.findByDocumentOrganization(organization, pageable);
        Page<ReviewResponseModel> reviewResponseModels = reviews.map(this::convertToReviewModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get reviews of organization successfully")
                .data(reviewResponseModels)
                .build());
    }

    @Operation(summary = "Kiểm tra đã đánh giá chưa",
            description = "Trả về kết quả người dùng đã đánh giá tài liệu này chưa")
    @GetMapping("/documents/{slug}/reviewed")
    public ResponseEntity<?> checkReviewed(@PathVariable String slug) {
        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));
        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        boolean isReviewed = reviewService.existsByUserAndDocument(user, document);
        String message = "Not reviewed";
        if (isReviewed) message = "Reviewed";

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(message)
                .build());
    }

    @Operation(summary = "Đánh giá một tài liệu",
            description = "Thực hiện đánh giá một tài liệu và trả về đánh giá vừa tạo")
    @PostMapping("/documents/{docId}/review")
    public ResponseEntity<?> reviewDocument(@PathVariable UUID docId, @RequestBody ReviewRequestModel reviewRequestModel) {
        Document document = documentService.findById(docId).orElseThrow(() -> new RuntimeException("Document not found"));
        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Review review = modelMapper.map(reviewRequestModel, Review.class);
        review.setDocument(document);
        review.setUser(user);
        review = reviewService.save(review);

        List<Review> reviewList = document.getReviews();
        int totalRating = reviewList
                .stream()
                .mapToInt(Review::getStar)
                .sum() + review.getStar();
        double averageRating = (double) totalRating / (reviewList.size() + 1);
        document.setAverageRating(averageRating);
        documentService.save(document);

        ReviewResponseModel reviewResponseModel = modelMapper.map(review, ReviewResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Review successfully")
                .data(reviewResponseModel)
                .build());
    }

    @Operation(summary = "Xoá đánh giá của một tài liệu",
            description = "Xoá một đánh giá khỏi hệ thống")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable UUID reviewId) {
        Review review = reviewService.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        int star = review.getStar();
        Document document = review.getDocument();

        reviewService.deleteById(reviewId);

        List<Review> reviewList = document.getReviews();
        int totalRating = reviewList
                .stream()
                .mapToInt(Review::getStar)
                .sum() - star;
        double averageRating = (double) totalRating / (reviewList.size() - 1);
        document.setAverageRating(averageRating);
        documentService.save(document);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Delete review successfully")
                .build());
    }

    private ReviewResponseModel convertToReviewModel(Object o) {
        ReviewResponseModel reviewResponseModel = modelMapper.map(o, ReviewResponseModel.class);
        return reviewResponseModel;
    }

}
