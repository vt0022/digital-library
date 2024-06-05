package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.request_model.ReviewRequestModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.ReviewResponseModel;
import com.major_project.digital_library.service.IReviewService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2")
public class ReviewController {
    private final IReviewService reviewService;

    @Autowired
    public ReviewController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Lấy đánh giá của một tài liệu",
            description = "Trả về tất cả đánh giá của một tài liệu")
    @GetMapping("/documents/{slug}/reviews")
    public ResponseEntity<?> getReviewsByDocument(@PathVariable String slug,
                                                  @RequestParam(defaultValue = "0") int rating,
                                                  @RequestParam(defaultValue = "0") int page) {
        Page<ReviewResponseModel> reviewResponseModels = reviewService.getReviewsByDocument(slug, rating, page);

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
        List<Object[]> reviewCounts = reviewService.countReviewsByStarOfDocument(slug);

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
    public ResponseEntity<?> getMyReviews(@RequestParam(defaultValue = "10") int status,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Page<ReviewResponseModel> reviewResponseModels = reviewService.getMyReviews(status, page, size);

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
        ReviewResponseModel reviewResponseModel = reviewService.approveReview(reviewId, isApproved, note);

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
        Page<ReviewResponseModel> reviewResponseModels = reviewService.getReviewsByOrganization(orgId, verifiedStatus, page, size);

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
        boolean isReviewed = reviewService.checkReviewed(slug);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(isReviewed ? "Reviewed" : "Not reviewed")
                .build());
    }

    @Operation(summary = "Đánh giá một tài liệu",
            description = "Thực hiện đánh giá một tài liệu và trả về đánh giá vừa tạo")
    @PostMapping("/documents/{docId}/review")
    public ResponseEntity<?> reviewDocument(@PathVariable UUID docId, @RequestBody ReviewRequestModel reviewRequestModel) {
        ReviewResponseModel reviewResponseModel = reviewService.reviewDocument(docId, reviewRequestModel);

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
        ReviewResponseModel reviewResponseModel = reviewService.editReview(reviewId, reviewRequestModel);

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
        reviewService.deleteReview(reviewId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Delete review successfully")
                .build());
    }
}
