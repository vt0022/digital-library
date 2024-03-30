package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyImage;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.request_model.ReplyRequestModel;
import com.major_project.digital_library.model.response_model.ReplyResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IPostService;
import com.major_project.digital_library.service.IReplyLikeService;
import com.major_project.digital_library.service.IReplyService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.util.GoogleDriveUpload;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class ReplyController {
    private final IReplyService replyService;
    private final IPostService postService;
    private final IUserService userService;
    private final IReplyLikeService replyLikeService;
    private final GoogleDriveUpload googleDriveUpload;
    private final ModelMapper modelMapper;

    @Autowired
    public ReplyController(IReplyService replyService, IPostService postService, IUserService userService, IReplyLikeService replyLikeService, GoogleDriveUpload googleDriveUpload, ModelMapper modelMapper) {
        this.replyService = replyService;
        this.postService = postService;
        this.userService = userService;
        this.replyLikeService = replyLikeService;
        this.googleDriveUpload = googleDriveUpload;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Xem phản hồi của một bài viết (khách)")
    @GetMapping("/posts/{postId}/replies/guest")
    public ResponseEntity<?> getPostRepliesForGuest(@PathVariable UUID postId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reply> replies = replyService.findAllByPostOrderByCreatedAtAsc(post, pageable);

        Page<ReplyResponseModel> replyResponseModels = replies.map(this::convertToReplyModelForGuest);

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
        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reply> replies = replyService.findAllByPostOrderByCreatedAtAsc(post, pageable);

        Page<ReplyResponseModel> replyResponseModels = replies.map(this::convertToReplyModel);

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
        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Reply parentReply = replyRequestModel.getParentReplyId() == null ? null : replyService.findById(replyRequestModel.getParentReplyId()).orElse(null);

        Reply reply = new Reply();
        reply.setContent(replyRequestModel.getContent());
        reply.setParentReply(parentReply);
        reply.setUser(user);
        reply.setPost(post);

        replyService.save(reply);

        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);
        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Add reply successfully")
                        .data(replyResponseModel)
                        .build());
    }

//    @Operation(summary = "Thêm một bình luận")
//    @PostMapping(path = "/posts/{postId}/reply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> addReply(@PathVariable UUID postId,
//                                      @RequestPart(name = "reply") ReplyRequestModel replyRequestModel,
//                                      @RequestPart(name = "images", required = false) List<MultipartFile> multipartFiles) {
//        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
//
//        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
//
//        Reply parentReply = replyRequestModel.getParentReplyId() == null ? null : replyService.findById(replyRequestModel.getParentReplyId()).orElse(null);
//
//        Reply reply = new Reply();
//        reply.setContent(replyRequestModel.getContent());
//        reply.setParentReply(parentReply);
//        reply.setUser(user);
//        reply.setPost(post);
//
//        if (multipartFiles != null)
//            for (MultipartFile file : multipartFiles) {
//                FileModel gd = googleDriveUpload.uploadImage(file, file.getOriginalFilename(), null, "reply");
//                ReplyImage replyImage = new ReplyImage();
//                replyImage.setUrl(gd.getViewUrl());
//                reply.getReplyImages().add(replyImage);
//            }
//
//        replyService.save(reply);
//
//        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);
//        return ResponseEntity.ok(
//                ResponseModel
//                        .builder()
//                        .status(200)
//                        .error(false)
//                        .message("Add reply successfully")
//                        .data(replyResponseModel)
//                        .build());
//    }

    @Operation(summary = "Chỉnh sửa một bình luận")
    @PutMapping(path = "/replies/{replyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editReply(@PathVariable UUID replyId,
                                       @RequestPart(name = "reply") ReplyRequestModel replyRequestModel,
                                       @RequestPart(name = "images", required = false) List<MultipartFile> multipartFiles) {

        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Reply reply = replyService.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));

        modelMapper.map(replyRequestModel, reply);

        if (multipartFiles != null)
            for (MultipartFile file : multipartFiles) {
                FileModel gd = googleDriveUpload.uploadImage(file, file.getOriginalFilename(), null, "reply");
                ReplyImage replyImage = new ReplyImage();
                replyImage.setUrl(gd.getViewUrl());
                reply.getReplyImages().add(replyImage);
            }

        replyService.save(reply);

        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);
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
        Reply reply = replyService.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));

        replyService.deleteById(replyId);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Delete reply successfully")
                        .build());
    }

    private ReplyResponseModel convertToReplyModelForGuest(Reply reply) {
        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);
        replyResponseModel.setTotalLikes(reply.getReplyLikes().size());
        return replyResponseModel;
    }

    private ReplyResponseModel convertToReplyModel(Reply reply) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
        boolean isLiked = replyLikeService.existsByUserAndReply(user, reply);
        boolean isMy = reply.getUser().getUserId().equals(user.getUserId());

        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);
        replyResponseModel.setLiked(isLiked);
        replyResponseModel.setMy(isMy);
        replyResponseModel.setTotalLikes(reply.getReplyLikes().size());

        return replyResponseModel;
    }
}
