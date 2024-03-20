package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostImage;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.request_model.PostRequestModel;
import com.major_project.digital_library.model.response_model.PostResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IPostService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.util.GoogleDriveUpload;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {
    private final IPostService postService;
    private final IUserService userService;
    private final ModelMapper modelMapper;
    private final GoogleDriveUpload googleDriveUpload;

    @Autowired
    public PostController(IPostService postService, IUserService userService, ModelMapper modelMapper, GoogleDriveUpload googleDriveUpload) {
        this.postService = postService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.googleDriveUpload = googleDriveUpload;
    }

    @Operation(summary = "Hiển thị danh sách bài viết")
    @GetMapping
    public ResponseEntity<?> getAllPosts(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(defaultValue = "newest") String order) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Post post = modelMapper.map(postRequestModel, Post.class);
        post.setUserPosted(user);

        if (multipartFiles != null)
            for (MultipartFile file : multipartFiles) {
                FileModel gd = googleDriveUpload.uploadImage(file, file.getOriginalFilename(), null, "post");
                PostImage postImage = new PostImage();
                postImage.setUrl(gd.getViewUrl());
                post.getPostImages().add(postImage);
            }

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

    @Operation(summary = "Tạo bài viết mới")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(@RequestPart("post") PostRequestModel postRequestModel,
                                        @RequestPart(name = "images", required = false) List<MultipartFile> multipartFiles) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Post post = modelMapper.map(postRequestModel, Post.class);
        post.setUserPosted(user);

        if (multipartFiles != null)
            for (MultipartFile file : multipartFiles) {
                FileModel gd = googleDriveUpload.uploadImage(file, file.getOriginalFilename(), null, "post");
                PostImage postImage = new PostImage();
                postImage.setUrl(gd.getViewUrl());
                post.getPostImages().add(postImage);
            }

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
    @PutMapping(path = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePost(@PathVariable UUID postId,
                                        @RequestPart("post") PostRequestModel postRequestModel,
                                        @RequestPart(name = "images", required = false) List<MultipartFile> multipartFiles) {
        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        post.setTitle(postRequestModel.getTitle());
        post.setContent(postRequestModel.getContent());

        if (multipartFiles != null)
            for (MultipartFile file : multipartFiles) {
                FileModel gd = googleDriveUpload.uploadImage(file, file.getOriginalFilename(), null, "post");
                PostImage postImage = new PostImage();
                postImage.setUrl(gd.getViewUrl());
                post.getPostImages().add(postImage);
            }

        postService.save(post);

        PostResponseModel postResponseModel = modelMapper.map(post, PostResponseModel.class);
        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Update post successfully")
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

    private PostResponseModel convertToPostModel(Object o) {
        PostResponseModel postResponseModel = modelMapper.map(o, PostResponseModel.class);
        return postResponseModel;
    }
}
