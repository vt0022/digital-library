package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.lean_model.ReplyLeanModel;
import com.major_project.digital_library.model.request_model.PostRequestModel;
import com.major_project.digital_library.model.response_model.DetailPostResponseModel;
import com.major_project.digital_library.model.response_model.PostResponseModel;
import com.major_project.digital_library.repository.*;
import com.major_project.digital_library.service.IPostService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

@Service
public class PostServiceImpl implements IPostService {
    private final IPostRepository postRepository;
    private final IPostLikeRepository postLikeRepository;
    private final IPostHistoryRepository postHistoryRepository;
    private final IUserRepositoty userRepository;
    private final ISubsectionRepository subsectionRepository;
    private final ILabelRepository labelRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public PostServiceImpl(IPostRepository postRepository, IPostLikeRepository postLikeRepository, IPostHistoryRepository postHistoryRepository, IUserRepositoty userRepository, ISubsectionRepository subsectionRepository, ILabelRepository labelRepository, IUserService userService, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.postHistoryRepository = postHistoryRepository;
        this.userRepository = userRepository;
        this.subsectionRepository = subsectionRepository;
        this.labelRepository = labelRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public <S extends Post> S save(S entity) {
        return postRepository.save(entity);
    }

    @Override
    public Optional<Post> findById(UUID uuid) {
        return postRepository.findById(uuid);
    }

    @Override
    public void deleteById(UUID uuid) {
        postRepository.deleteById(uuid);
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public DetailPostResponseModel getPostDetail(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        DetailPostResponseModel detailPostResponseModel = convertToDetailPostModel(post);
        return detailPostResponseModel;
    }

    @Override
    public PostResponseModel getPostDetailForGuest(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        PostResponseModel postResponseModel = convertToPostModel(post);
        return postResponseModel;
    }

    @Override
    public Page<PostResponseModel> findPosts(int page, int size, String order, String subsectionSlug, String labelSlug, String query) {
        Subsection subsection = null;
        Label label = null;
        if (!subsectionSlug.equals(""))
            subsection = subsectionRepository.findBySlug(subsectionSlug).orElseThrow(() -> new RuntimeException("Subsection not found"));
        if (!labelSlug.equals(""))
            label = labelRepository.findBySlug(labelSlug).orElseThrow(() -> new RuntimeException("Label not found"));

        Page<Post> posts = Page.empty();
        if (order.equals("mostViewed")) {
            Sort sort = Sort.by(Sort.Direction.DESC, "totalViews");
            Pageable pageable = PageRequest.of(page, size, sort);
            posts = postRepository.findAllPosts(subsection, label, query, pageable);
        } else if (order.equals("leastViewed")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "totalViews");
            Pageable pageable = PageRequest.of(page, size, sort);
            posts = postRepository.findAllPosts(subsection, label, query, pageable);
        } else if (order.equals("mostLiked")) {
            Pageable pageable = PageRequest.of(page, size);
            posts = postRepository.findAllPostsOrderByTotalLikesDesc(subsection, label, query, pageable);
        } else if (order.equals("leastLiked")) {
            Pageable pageable = PageRequest.of(page, size);
            posts = postRepository.findAllPostsOrderByTotalLikesAsc(subsection, label, query, pageable);
        } else if (order.equals("mostReplied")) {
            Pageable pageable = PageRequest.of(page, size);
            posts = postRepository.findAllPostsOrderByTotalRepliesDesc(subsection, label, query, pageable);
        } else if (order.equals("leastReplied")) {
            Pageable pageable = PageRequest.of(page, size);
            posts = postRepository.findAllPostsOrderByTotalRepliesAsc(subsection, label, query, pageable);
        } else if (order.equals("oldest")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            posts = postRepository.findAllPosts(subsection, label, query, pageable);
        } else {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            posts = postRepository.findAllPosts(subsection, label, query, pageable);
        }

        return posts.map(this::convertToPostModel);
    }

    @Override
    public Page<PostResponseModel> findPostsOfUser(UUID userId, int page, int size, String query) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByUser(user, query, pageable);
        Page<PostResponseModel> postResponseModels = posts.map(this::convertToPostModel);

        return postResponseModels;
    }

    @Override
    public PostResponseModel addPost(PostRequestModel postRequestModel) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Post post = modelMapper.map(postRequestModel, Post.class);
        post.setUserPosted(user);
        postRepository.save(post);

        PostResponseModel postResponseModel = modelMapper.map(post, PostResponseModel.class);

        return postResponseModel;
    }

    @Override
    public PostResponseModel editPost(UUID postId, PostRequestModel postRequestModel) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserPosted().getUserId().equals(user.getUserId()))
            return null;

        PostHistory postHistory = modelMapper.map(post, PostHistory.class);
        postHistory.setPost(post);
        postHistoryRepository.save(postHistory);

        post.setTitle(postRequestModel.getTitle());
        post.setContent(postRequestModel.getContent());
        postRepository.save(post);

        PostResponseModel postResponseModel = modelMapper.map(post, PostResponseModel.class);

        return postResponseModel;
    }

    @Override
    public void deletePost(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(post);
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

        boolean isLiked = postLikeRepository.existsByUserAndPost(user, post);
        boolean isMy = post.getUserPosted().getUserId().equals(user.getUserId());

        detailPostResponseModel.setLiked(isLiked);
        detailPostResponseModel.setMy(isMy);
        detailPostResponseModel.setTotalLikes(post.getPostLikes().size());
        detailPostResponseModel.setTotalReplies(post.getReplies().size());

        return detailPostResponseModel;
    }
}
