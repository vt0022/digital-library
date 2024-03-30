package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IPostLikeService;
import com.major_project.digital_library.service.IPostService;
import com.major_project.digital_library.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PostLikeController {
    private final IPostLikeService postLikeService;
    private final IUserService userService;
    private final IPostService postService;

    @Autowired
    public PostLikeController(IPostLikeService postLikeService, IUserService userService, IPostService postService) {
        this.postLikeService = postLikeService;
        this.userService = userService;
        this.postService = postService;
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable UUID postId) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Post post = postService.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostLike> postLike = postLikeService.findByUserAndPost(user, post);

        if (postLike.isPresent()) {
            postLikeService.delete(postLike.get());
        } else {
            PostLike newPostLike = new PostLike();
            newPostLike.setUser(user);
            newPostLike.setPost(post);
            postLikeService.save(newPostLike);
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message((postLike.isPresent() ? "Unlike " : "Like ") + "document successfully")
                .build());
    }
}
