package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.request_model.ReplyRequestModel;
import com.major_project.digital_library.model.response_model.ReplyHistoryResponseModel;
import com.major_project.digital_library.model.response_model.ReplyResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IReplyHistoryService;
import com.major_project.digital_library.service.IReplyLikeService;
import com.major_project.digital_library.service.IReplyService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2")
public class ReplyController {
    private final IReplyService replyService;
    private final IReplyHistoryService replyHistoryService;
    private final IReplyLikeService replyLikeService;

    @Autowired
    public ReplyController(IReplyService replyService, IReplyHistoryService replyHistoryService, IReplyLikeService replyLikeService) {
        this.replyService = replyService;
        this.replyHistoryService = replyHistoryService;
        this.replyLikeService = replyLikeService;
    }

    @Operation(summary = "Xem phản hồi của một bài viết (khách)")
    @GetMapping("/posts/{postId}/replies/guest")
    public ResponseEntity<?> getPostRepliesForGuest(@PathVariable UUID postId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Page<ReplyResponseModel> replyResponseModels = replyService.getRepliesOfPostForGuest(postId, page, size);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get post's replies successfully")
                        .data(replyResponseModels)
                        .build());
    }

    @Operation(summary = "Xem phản hồi của một bài viết (đăng nhập)")
    @GetMapping("/posts/{postId}/replies")
    public ResponseEntity<?> getPostReplies(@PathVariable UUID postId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Page<ReplyResponseModel> replyResponseModels = replyService.getRepliesOfPost(postId, page, size);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get post's replies successfully")
                        .data(replyResponseModels)
                        .build());
    }

    @Operation(summary = "Thêm một bình luận")
    @PostMapping("/posts/{postId}/reply")
    public ResponseEntity<?> addReply(@PathVariable UUID postId,
                                      @RequestBody ReplyRequestModel replyRequestModel) {
        ReplyResponseModel replyResponseModel = replyService.addReply(postId, replyRequestModel);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Add reply successfully")
                        .data(replyResponseModel)
                        .build());
    }

    @Operation(summary = "Chỉnh sửa một bình luận")
    @PutMapping("/replies/{replyId}")
    public ResponseEntity<?> editReply(@PathVariable UUID replyId,
                                       @RequestBody Map<String, String> replyContent) {
        ReplyResponseModel replyResponseModel = replyService.editReply(replyId, replyContent);

        if (replyResponseModel == null)
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(true)
                    .message("Reply not accessible")
                    .build());
        else
            return ResponseEntity.ok(
                    ResponseModel
                            .builder()
                            .status(200)
                            .error(false)
                            .message("Edit reply successfully")
                            .data(replyResponseModel)
                            .build());
    }

    @Operation(summary = "Xoá một bình luận")
    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<?> editReply(@PathVariable UUID replyId) {
        replyService.deleteReply(replyId);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Delete reply successfully")
                        .build());
    }

    @Operation(summary = "Xem tất cả phản hồi của một người dùng")
    @GetMapping("/replies/user/{userId}")
    public ResponseEntity<?> getRepliesOfUser(@PathVariable UUID userId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "5") int size) {
        Page<ReplyResponseModel> replyResponseModels = replyService.getRepliesOfUser(userId, page, size);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get user's replies successfully")
                        .data(replyResponseModels)
                        .build());
    }

    @PostMapping("/replies/{replyId}/like")
    public ResponseEntity<?> likeReply(@PathVariable UUID replyId) {
        boolean isLiked = replyLikeService.likeReply(replyId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message((isLiked ? "Unlike " : "Like ") + "reply successfully")
                .build());
    }

    @Operation(summary = "Xem lịch sử chỉnh sửa của bình luận")
    @GetMapping("/replies/{replyId}/history")
    public ResponseEntity<?> getRepliesOfUser(@PathVariable UUID replyId) {
        List<ReplyHistoryResponseModel> replyHistoryResponseModels = replyHistoryService.findHistoryOfReply(replyId);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get history of reply successfully")
                        .data(replyHistoryResponseModels)
                        .build());
    }

}
