package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.lean_model.ReplyLeanModel;
import com.major_project.digital_library.model.request_model.PostRequestModel;
import com.major_project.digital_library.model.response_model.DetailPostResponseModel;
import com.major_project.digital_library.model.response_model.PostResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IPostLikeService;
import com.major_project.digital_library.service.IPostService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.util.GoogleDriveUpload;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final IPostService postService;
    private final IUserService userService;
    private final IPostLikeService postLikeService;
    private final ModelMapper modelMapper;
    private final GoogleDriveUpload googleDriveUpload;

    @Autowired
    public PostController(IPostService postService, IUserService userService, IPostLikeService postLikeService, ModelMapper modelMapper, GoogleDriveUpload googleDriveUpload) {
        this.postService = postService;
        this.userService = userService;
        this.postLikeService = postLikeService;
        this.modelMapper = modelMapper;
        this.googleDriveUpload = googleDriveUpload;
    }

    @Operation(summary = "Hiển thị chi tiết bài viết cho khách")
    @GetMapping("/{postId}/guest")
    public ResponseEntity<?> getPostDetailForGuest(@PathVariable UUID postId) {
        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        PostResponseModel postResponseModel = convertToPostModel(post);
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

        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        DetailPostResponseModel detailPostResponseModel = convertToDetailPostModel(post);

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
    public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "newest") String order,
                                         @RequestParam(defaultValue = "") String s) {
        Page<Post> posts = postService.findPosts(page, size, order, s);
        Page<PostResponseModel> postResponseModels = posts.map(this::convertToPostModel);

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
                                            @RequestParam(defaultValue = "10") int size) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postService.findAllByUserPostedOrderByCreatedAtDesc(user, pageable);
        Page<PostResponseModel> postResponseModels = posts.map(this::convertToPostModel);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Get posts successfully")
                        .data(postResponseModels)
                        .build());
    }

    @Operation(summary = "Tạo bài viết mới")
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostRequestModel postRequestModel) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Post post = modelMapper.map(postRequestModel, Post.class);
        post.setUserPosted(user);

        postService.save(post);

        PostResponseModel postResponseModel = modelMapper.map(post, PostResponseModel.class);
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
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserPosted().getUserId().equals(user.getUserId()))
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(true)
                    .message("Post not accessible")
                    .build());

        post.setTitle(postRequestModel.getTitle());
        post.setContent(postRequestModel.getContent());

        postService.save(post);

        PostResponseModel postResponseModel = modelMapper.map(post, PostResponseModel.class);
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
        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        postService.deleteById(postId);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Delete post successfully")
                        .build());
    }

    private PostResponseModel convertToPostModel(Post post) {
        PostResponseModel postResponseModel = modelMapper.map(post, PostResponseModel.class);

        Reply latestReply = post.getReplies()
                .stream()
                .max(Comparator.comparing(Reply::getCreatedAt)).orElse(null);

        postResponseModel.setTotalLikes(post.getPostLikes().size());
        postResponseModel.setTotalReplies(post.getReplies().size());
        postResponseModel.setLatestReply(
                latestReply == null ? null :
                        modelMapper.map(latestReply, ReplyLeanModel.class));

        return postResponseModel;
    }

    private DetailPostResponseModel convertToDetailPostModel(Post post) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        DetailPostResponseModel detailPostResponseModel = modelMapper.map(post, DetailPostResponseModel.class);

        boolean isLiked = postLikeService.existsByUserAndPost(user, post);
        boolean isMy = post.getUserPosted().getUserId().equals(user.getUserId());

        detailPostResponseModel.setLiked(isLiked);
        detailPostResponseModel.setMy(isMy);
        detailPostResponseModel.setTotalLikes(post.getPostLikes().size());
        detailPostResponseModel.setTotalReplies(post.getReplies().size());

        return detailPostResponseModel;
    }
}
