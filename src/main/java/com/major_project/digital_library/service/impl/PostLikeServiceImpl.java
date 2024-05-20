package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.BadgeUnit;
import com.major_project.digital_library.constant.NotificationMessage;
import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.PostLikeResponseModel;
import com.major_project.digital_library.repository.IPostLikeRepository;
import com.major_project.digital_library.repository.IPostRepository;
import com.major_project.digital_library.service.IBadgeRewardService;
import com.major_project.digital_library.service.INotificationService;
import com.major_project.digital_library.service.IPostLikeService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PostLikeServiceImpl implements IPostLikeService {
    private final IPostLikeRepository postLikeRepository;
    private final IPostRepository postRepository;
    private final IUserService userService;
    private final IBadgeRewardService badgeRewardService;
    private final INotificationService notificationService;
    private final ModelMapper modelMapper;

    @Autowired
    public PostLikeServiceImpl(IPostLikeRepository postLikeRepository, IPostRepository postRepository, IUserService userService, IBadgeRewardService badgeRewardService, INotificationService notificationService, ModelMapper modelMapper) {
        this.postLikeRepository = postLikeRepository;
        this.postRepository = postRepository;
        this.userService = userService;
        this.badgeRewardService = badgeRewardService;
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
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

            if (!post.getUserPosted().getUserId().equals(user.getUserId()))
                notificationService.sendNotification(NotificationMessage.LIKE_POST.name(), NotificationMessage.LIKE_POST.getMessage(), user, post.getUserPosted(), post);

            return false;
        }
    }

    @Override
    public Page<PostLikeResponseModel> findByUser(int page, int size) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Pageable pageable = PageRequest.of(page, size);
        Page<PostLike> postLikes = postLikeRepository.findAllByUser(user, pageable);
        Page<PostLikeResponseModel> postLikeResponseModels = postLikes.map(this::convertToPostLikeModel);

        return postLikeResponseModels;
    }

    private PostLikeResponseModel convertToPostLikeModel(PostLike postLike) {
        PostLikeResponseModel postLikeResponseModel = modelMapper.map(postLike, PostLikeResponseModel.class);

        return postLikeResponseModel;
    }
}
