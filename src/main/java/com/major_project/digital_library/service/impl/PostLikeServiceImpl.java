package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.BadgeUnit;
import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IPostLikeRepository;
import com.major_project.digital_library.repository.IPostRepository;
import com.major_project.digital_library.service.IBadgeRewardService;
import com.major_project.digital_library.service.IPostLikeService;
import com.major_project.digital_library.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PostLikeServiceImpl implements IPostLikeService {
    private final IPostLikeRepository postLikeRepository;
    private final IPostRepository postRepository;
    private final IUserService userService;
    private final IBadgeRewardService badgeRewardService;

    @Autowired
    public PostLikeServiceImpl(IPostLikeRepository postLikeRepository, IPostRepository postRepository, IUserService userService, IBadgeRewardService badgeRewardService) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.badgeRewardService = badgeRewardService;
    }

    @Override
    public boolean existsByUserAndPost(User user, Post post) {
        return postLikeRepository.existsByUserAndPost(user, post);
    }

    @Override
    public <S extends PostLike> S save(S entity) {
        return postLikeRepository.save(entity);
    }

    @Override
    public void deleteByUserAndPost(User user, Post post) {
        postLikeRepository.deleteByUserAndPost(user, post);
    }

    @Override
    public void delete(PostLike entity) {
        postLikeRepository.delete(entity);
    }

    @Override
    public Optional<PostLike> findByUserAndPost(User user, Post post) {
        return postLikeRepository.findByUserAndPost(user, post);
    }

    @Override
    public boolean likePost(UUID postId) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostLike> postLike = postLikeRepository.findByUserAndPost(user, post);

        if (postLike.isPresent()) {
            postLikeRepository.delete(postLike.get());
            return true;
        } else {
            PostLike newPostLike = new PostLike();
            newPostLike.setUser(user);
            newPostLike.setPost(post);
            postLikeRepository.save(newPostLike);

            badgeRewardService.rewardBadge(post.getUserPosted(), BadgeUnit.TOTAL_POST_LIKES.name());
            
            return false;
        }
    }
}
