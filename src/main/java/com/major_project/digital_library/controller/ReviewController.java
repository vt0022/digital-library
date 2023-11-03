package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.ReviewRequestModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.ReviewResponseModel;
import com.major_project.digital_library.service.IDocumentService;
import com.major_project.digital_library.service.IReviewService;
import com.major_project.digital_library.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {
    private final IReviewService reviewService;
    private final IDocumentService documentService;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReviewController(IReviewService reviewService, IDocumentService documentService, IUserService userService, ModelMapper modelMapper) {
        this.reviewService = reviewService;
        this.documentService = documentService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Lấy đánh giá của một tài liệu",
            description = "Trả về tất cả đánh giá của một tài liệu")
    @GetMapping("/documents/{docId}/reviews")
    public ResponseEntity<?> getReviewsByDocument(@PathVariable UUID docId) {
        Document document = documentService.findById(docId).orElseThrow(() -> new RuntimeException("Document not found"));
        // Find user info
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(auth.getPrincipal());
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Email is not valid"));
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

    @Operation(summary = "Đánh giá một tài liệu",
            description = "Thực hiện đánh giá một tài liệu và trả về đánh giá vừa tạo")
    @PostMapping("/documents/{docId}/review")
    public ResponseEntity<?> reviewDocument(@PathVariable UUID docId, @RequestBody ReviewRequestModel reviewRequestModel) {
        Document document = documentService.findById(docId).orElseThrow(() -> new RuntimeException("Document not found"));
        // Find user info
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(auth.getPrincipal());
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Email is not valid"));

        Review review = modelMapper.map(reviewRequestModel, Review.class);
        review.setDocument(document);
        review.setUser(user);
        review = reviewService.save(review);
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
    @DeleteMapping("/documents/{docId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable UUID docId, @PathVariable UUID reviewId) {
        reviewService.deleteById(reviewId);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Delete review successfully")
                .build());
    }
}
