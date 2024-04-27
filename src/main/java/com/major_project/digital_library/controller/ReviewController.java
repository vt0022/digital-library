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

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2")
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
    public ResponseEntity<?> getReviewsByDocument(@PathVariable String slug,
                                                  @RequestParam(defaultValue = "0") int rating) {
        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));
//
//        List<ReviewResponseModel> reviewResponseModels = document.getReviews()
//                .stream()
//                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
//                .map(reviewResponse -> modelMapper.map(reviewResponse, ReviewResponseModel.class))
//                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(0, 100);
        List<Review> reviews = new ArrayList<>();

        if (rating >= 1 && rating <= 5)
            reviews = reviewService.findByDocumentAndStarAndVerifiedStatusOrderByCreatedAt(document, rating, 1, pageable).getContent();
        else
            reviews = reviewService.findByDocumentAndVerifiedStatusOrderByCreatedAt(document, 1, pageable).getContent();

        List<ReviewResponseModel> reviewResponseModels = reviews.stream().map(this::convertToReviewModel).collect(Collectors.toList());

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get reviews of documents with rating successfully")
                .data(reviewResponseModels)
                .build());
    }

    @Operation(summary = "Đếm đánh giá dựa trên số sao",
            description = "Trả về số lượng đánh giá dựa trên số sao của một tài liệu")
    @GetMapping("/documents/{slug}/reviews/count")
    public ResponseEntity<?> countReviewsByStarOfDocument(@PathVariable String slug) {
        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        List<Object[]> reviewCounts = reviewService.countReviewsByStarAndDocument(document);

        Set<Integer> existingStars = new HashSet<>();
        for (Object[] review : reviewCounts) {
            existingStars.add((Integer) review[0]);
        }

        for (int i = 1; i <= 5; i++) {
            if (!existingStars.contains(i)) {
                reviewCounts.add(new Object[]{i, 0});
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get reviews of documents with rating successfully")
                .data(reviewCounts)
                .build());
    }

    @Operation(summary = "Lấy đánh giá của người dùng hiện tại",
            description = "Trả về tất cả đánh giá của người dùng hiện tại")
    @GetMapping("/reviews/mine")
    public ResponseEntity<?> getMyReviews(@RequestParam(defaultValue = "10") int status) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(0, 100);

        List<Review> reviews = new ArrayList<>();

        if (status >= -1 && status <= 1)
            reviews = reviewService.findByUserAndVerifiedStatusOrderByCreatedAt(user, status, pageable).getContent();
        else
            reviews = reviewService.findByUserOrderByCreatedAt(user, pageable).getContent();

        List<ReviewResponseModel> reviewResponseModels = reviews.stream().map(this::convertToReviewModel).collect(Collectors.toList());

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get reviews of current user successfully")
                .data(reviewResponseModels)
                .build());
    }

    @Operation(summary = "Duyệt đánh giá",
            description = "Duyệt đánh giá của người dùng")
    @PutMapping("/reviews/{reviewId}/approval")
    public ResponseEntity<?> approveReview(@PathVariable UUID reviewId,
                                           @RequestParam boolean isApproved,
                                           @RequestParam(required = false, defaultValue = "") String note) {
        Review review = reviewService.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));

        review.setVerifiedStatus(isApproved ? 1 : -1);
        if (!isApproved)
            review.setNote(note);
        review = reviewService.save(review);

        ReviewResponseModel reviewResponseModel = modelMapper.map(review, ReviewResponseModel.class);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(isApproved ? "Duyệt đánh giá thành công" : "Từ chối đánh giá thành công")
                .data(reviewResponseModel)
                .build());
    }

    @Operation(summary = "Lấy đánh giá của một trường",
            description = "Trả về tất cả đánh giá của một trường")
    @GetMapping("/organizations/{orgId}/reviews")
    public ResponseEntity<?> getReviewsByOrganization(@PathVariable UUID orgId,
                                                      @RequestParam(defaultValue = "10", required = false) int verifiedStatus,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "15") int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Organization organization = organizationService.findById(orgId).orElseThrow(() -> new RuntimeException("Organization not found"));

        Page<Review> reviews = Page.empty();
        if (verifiedStatus >= -1 && verifiedStatus <= 1)
            reviews = reviewService.findByVerifiedStatusAndDocumentOrganization(verifiedStatus, organization, pageable);
        else
            reviews = reviewService.findByDocumentOrganization(organization, pageable);

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
        review.setVerifiedStatus(0);
        review = reviewService.save(review);

        ReviewResponseModel reviewResponseModel = modelMapper.map(review, ReviewResponseModel.class);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Review successfully. Wait for approval.")
                .data(reviewResponseModel)
                .build());
    }

    @Operation(summary = "Chỉnh sửa đánh giá của một tài liệu",
            description = "Thực hiện chỉnh sửa đánh giá của một tài liệu và trả về đánh giá vừa sửa")
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<?> editReview(@PathVariable UUID reviewId, @RequestBody ReviewRequestModel reviewRequestModel) {
        Review review = reviewService.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));

        review.setVerifiedStatus(0);
        review.setStar(reviewRequestModel.getStar());
        review.setContent(reviewRequestModel.getContent());
        review = reviewService.save(review);

        ReviewResponseModel reviewResponseModel = modelMapper.map(review, ReviewResponseModel.class);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Review successfully. Wait for approval.")
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
