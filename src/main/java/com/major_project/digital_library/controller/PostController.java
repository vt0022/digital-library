package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.request_model.PostRequestModel;
import com.major_project.digital_library.model.response_model.*;
import com.major_project.digital_library.service.IPostHistoryService;
import com.major_project.digital_library.service.IPostLikeService;
import com.major_project.digital_library.service.IPostService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.service.other.GoogleDriveService;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/posts")
public class PostController {
    private final IPostService postService;
    private final IUserService userService;
    private final IPostLikeService postLikeService;
    private final IPostHistoryService postHistoryService;
    private final ModelMapper modelMapper;
    private final GoogleDriveService googleDriveService;

    @Autowired
    public PostController(IPostService postService, IUserService userService, IPostLikeService postLikeService, IPostHistoryService postHistoryService, ModelMapper modelMapper, GoogleDriveService googleDriveService) {
        this.postService = postService;
        this.userService = userService;
        this.postLikeService = postLikeService;
        this.postHistoryService = postHistoryService;
        this.modelMapper = modelMapper;
        this.googleDriveService = googleDriveService;
    }

    @Operation(summary = "Hiển thị chi tiết bài viết cho khách")
    @GetMapping("/{postId}/guest")
    public ResponseEntity<?> getPostDetailForGuest(@PathVariable UUID postId) {
        PostResponseModel postResponseModel = postService.getPostDetailForGuest(postId);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get post detail successfully")
                        .data(postResponseModel)
                        .build());
    }

    @Operation(summary = "Hiển thị chi tiết bài viết cho người dùng đăng nhập")
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable UUID postId) {
        DetailPostResponseModel detailPostResponseModel = postService.getPostDetail(postId);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get post detail successfully")
                        .data(detailPostResponseModel)
                        .build());
    }

    @Operation(summary = "Hiển thị danh sách bài viết")
    @GetMapping
    public ResponseEntity<?> getPosts(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "newest") String order,
                                      @RequestParam(defaultValue = "") String subsection,
                                      @RequestParam(defaultValue = "") String label,
                                      @RequestParam(defaultValue = "") String s) {
        Page<PostResponseModel> postResponseModels = postService.findViewablePosts(page, size, order, subsection, label, s);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get posts successfully")
                        .data(postResponseModels)
                        .build());
    }

    @Operation(summary = "Hiển thị toàn bộ danh sách bài viết")
    @GetMapping("/all")
    public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "newest") String order,
                                         @RequestParam(defaultValue = "") String subsection,
                                         @RequestParam(defaultValue = "") String label,
                                         @RequestParam(defaultValue = "") String s) {
        Page<PostResponseModel> postResponseModels = postService.findAllPosts(page, size, order, subsection, label, s);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get posts successfully")
                        .data(postResponseModels)
                        .build());
    }

    @Operation(summary = "Lấy danh sách bài đăng của một người dùng")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUser(@PathVariable UUID userId,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "") String query) {
        Page<PostResponseModel> postResponseModels = postService.findViewablePostsOfUser(userId, page, size, query);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get posts of user successfully")
                        .data(postResponseModels)
                        .build());
    }

    @Operation(summary = "Lấy toàn bộ danh sách bài đăng của một người dùng")
    @GetMapping("/all/user/{userId}")
    public ResponseEntity<?> getAllPostsByUser(@PathVariable UUID userId,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "") String query) {
        Page<PostResponseModel> postResponseModels = postService.findAllPostsOfUser(userId, page, size, query);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get posts of user successfully")
                        .data(postResponseModels)
                        .build());
    }

    @Operation(summary = "Tạo bài viết mới")
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostRequestModel postRequestModel) {
        PostResponseModel postResponseModel = postService.addPost(postRequestModel);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Create post successfully")
                        .data(postResponseModel)
                        .build());
    }

    @Operation(summary = "Chỉnh sửa một bài viết")
    @PutMapping("/{postId}")
    public ResponseEntity<?> editPost(@PathVariable UUID postId,
                                      @RequestBody PostRequestModel postRequestModel) {
        PostResponseModel postResponseModel = postService.editPost(postId, postRequestModel);

        if (postResponseModel == null)
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(true)
                    .message("Post not accessible")
                    .build());
        else
            return ResponseEntity.ok(
                    ResponseModel
                            .builder()
                            .status(200)
                            .error(false)
                            .message("Edit post successfully")
                            .data(postResponseModel)
                            .build());
    }

    @Operation(summary = "Xoá bỏ bài viết")
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable UUID postId) {
        boolean isDeleted = postService.deletePost(postId);

        if (isDeleted)
            return ResponseEntity.ok(
                    ResponseModel
                            .builder()
                            .status(200)
                            .error(false)
                            .message("Delete post successfully")
                            .build());
        else
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(true)
                    .message("Post not accessible")
                    .build());
    }

    @Operation(summary = "Thích bài viết")
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable UUID postId) {
        boolean isLiked = postLikeService.likePost(postId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message((isLiked ? "Unlike " : "Like ") + "post successfully")
                .build());
    }

    @Operation(summary = "Xem lịch sử chỉnh sửa của bài viết")
    @GetMapping("/{postId}/history")
    public ResponseEntity<?> getHistoryOfPost(@PathVariable UUID postId) {
        List<PostHistoryResponseModel> postHistoryResponseModels = postHistoryService.findHistoryOfPost(postId);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get history of post successfully")
                        .data(postHistoryResponseModels)
                        .build());
    }

    @Operation(summary = "Tìm bài viết liên quan")
    @GetMapping("/related")
    public ResponseEntity<?> getRelatedPosts(@RequestParam(defaultValue = "") String query) {
        Page<PostResponseModel> postResponseModels = postService.findRelatedPosts(query);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get related posts successfully")
                        .data(postResponseModels)
                        .build());
    }

    @Operation(summary = "Lấy toàn bộ danh sách bài đăng đã thích của một người dùng")
    @GetMapping("/user/likes")
    public ResponseEntity<?> getAllPostLikesByUser(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        Page<PostLikeResponseModel> postLikeResponseModels = postLikeService.findByUser(page, size);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get post likes of user successfully")
                        .data(postLikeResponseModels)
                        .build());
    }
}
