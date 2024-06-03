package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.BadgeUnit;
import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.lean_model.BadgeLeanModel;
import com.major_project.digital_library.model.lean_model.ReplyLeanModel;
import com.major_project.digital_library.model.request_model.PostRequestModel;
import com.major_project.digital_library.model.response_model.DetailPostResponseModel;
import com.major_project.digital_library.model.response_model.PostResponseModel;
import com.major_project.digital_library.repository.*;
import com.major_project.digital_library.service.IBadgeRewardService;
import com.major_project.digital_library.service.IBadgeService;
import com.major_project.digital_library.service.IPostService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.util.SlugGenerator;
import com.major_project.digital_library.yake.TagExtractor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements IPostService {
    private final IPostRepository postRepository;
    private final IPostLikeRepository postLikeRepository;
    private final IPostHistoryRepository postHistoryRepository;
    private final IUserRepository userRepository;
    private final ISubsectionRepository subsectionRepository;
    private final ILabelRepository labelRepository;
    private final ITagRepository tagRepository;
    private final IPostAcceptanceRepository postAcceptanceRepository;
    private final IUserService userService;
    private final IBadgeService badgeService;
    private final IBadgeRewardService badgeRewardService;
    private final ModelMapper modelMapper;
    private final TagExtractor tagExtractor;

    @Autowired
    public PostServiceImpl(IPostRepository postRepository, IPostLikeRepository postLikeRepository, IPostHistoryRepository postHistoryRepository, IUserRepository userRepository, ISubsectionRepository subsectionRepository, ILabelRepository labelRepository, ITagRepository tagRepository, IPostAcceptanceRepository postAcceptanceRepository, IUserService userService, IBadgeService badgeService, IBadgeRewardService badgeRewardService, ModelMapper modelMapper, TagExtractor tagExtractor) {
        this.postRepository = postRepository;
        this.postLikeRepository = postLikeRepository;
        this.postHistoryRepository = postHistoryRepository;
        this.userRepository = userRepository;
        this.subsectionRepository = subsectionRepository;
        this.labelRepository = labelRepository;
        this.tagRepository = tagRepository;
        this.postAcceptanceRepository = postAcceptanceRepository;
        this.userService = userService;
        this.badgeService = badgeService;
        this.badgeRewardService = badgeRewardService;
        this.modelMapper = modelMapper;
        this.tagExtractor = tagExtractor;
    }

    @Override
    public DetailPostResponseModel getPostDetail(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        if (!post.isDisabled()) {
            post.setTotalViews(post.getTotalViews() + 1);
            postRepository.save(post);
        }

        badgeRewardService.rewardBadge(post.getUserPosted(), String.valueOf(BadgeUnit.TOTAL_POST_VIEWS));

        DetailPostResponseModel detailPostResponseModel = convertToDetailPostModel(post);

        return detailPostResponseModel;
    }

    @Override
    public PostResponseModel getPostDetailForGuest(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        boolean isLabelDisabled = post.getLabel() != null && post.getLabel().isDisabled();
        boolean isSectionDisabled = post.getSubsection() != null && post.getSubsection().getSection() != null && post.getSubsection().getSection().isDisabled();
        boolean isSubsectionDisabled = post.getSubsection() != null && post.getSubsection().isDisabled();

        if (!post.isDisabled() && !isLabelDisabled && !isSectionDisabled && !isSubsectionDisabled) {
            post.setTotalViews(post.getTotalViews() + 1);
            postRepository.save(post);

            badgeRewardService.rewardBadge(post.getUserPosted(), String.valueOf(BadgeUnit.TOTAL_POST_VIEWS));

            PostResponseModel postResponseModel = convertToPostModel(post);

            return postResponseModel;
        } else
            return null;
    }

    @Override
    public Page<PostResponseModel> findViewablePosts(int page, int size, String order, String subsectionSlug, String labelSlug, String query) {
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
            posts = postRepository.findViewablePosts(subsection, label, query, pageable);
        } else if (order.equals("leastViewed")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "totalViews");
            Pageable pageable = PageRequest.of(page, size, sort);
            posts = postRepository.findViewablePosts(subsection, label, query, pageable);
        } else if (order.equals("mostLiked")) {
            Pageable pageable = PageRequest.of(page, size);
            posts = postRepository.findViewablePostsOrderByTotalLikesDesc(subsection, label, query, pageable);
        } else if (order.equals("leastLiked")) {
            Pageable pageable = PageRequest.of(page, size);
            posts = postRepository.findViewablePostsOrderByTotalLikesAsc(subsection, label, query, pageable);
        } else if (order.equals("mostReplied")) {
            Pageable pageable = PageRequest.of(page, size);
            posts = postRepository.findViewablePostsOrderByTotalRepliesDesc(subsection, label, query, pageable);
        } else if (order.equals("leastReplied")) {
            Pageable pageable = PageRequest.of(page, size);
            posts = postRepository.findViewablePostsOrderByTotalRepliesAsc(subsection, label, query, pageable);
        } else if (order.equals("oldest")) {
            Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            posts = postRepository.findViewablePosts(subsection, label, query, pageable);
        } else {
            Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);
            posts = postRepository.findViewablePosts(subsection, label, query, pageable);
        }

        return posts.map(this::convertToPostModel);
    }

    @Override
    public Page<PostResponseModel> findAllPosts(int page, int size, String order, String subsectionSlug, String labelSlug, String query) {
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
    public Page<PostResponseModel> findViewablePostsOfUser(UUID userId, int page, int size, String query) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findViewablePostsByUser(user, query, pageable);
        Page<PostResponseModel> postResponseModels = posts.map(this::convertToPostModel);

        return postResponseModels;
    }

    @Override
    public Page<PostResponseModel> findAllPostsOfUser(UUID userId, int page, int size, String query) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findAllByUser(user, query, pageable);
        Page<PostResponseModel> postResponseModels = posts.map(this::convertToPostModel);

        return postResponseModels;
    }

    @Override
    public Page<PostResponseModel> findRelatedPosts(String query) {
        User user = userService.findLoggedInUser();

        List<String> tags = tagExtractor.findKeywords(query);

        Pageable pageable = PageRequest.of(0, 100);
        Page<Post> posts = Page.empty();
        if (user.getRole().getRoleName().equals("ROLE_ADMIN"))
            posts = postRepository.findAllByTags(tags, pageable);
        else
            posts = postRepository.findViewablePostsByTags(tags, pageable);
        Page<PostResponseModel> postResponseModels = posts.map(this::convertToPostModel);

        return postResponseModels;
    }

    @Override
    public Page<PostResponseModel> findRelatedPostsByAPost(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Post> posts = postRepository.findRelatedPostsByTags(post, pageable);

        Page<PostResponseModel> postResponseModels = posts.map(this::convertToPostModel);

        return postResponseModels;
    }

    @Override
    public PostResponseModel addPost(PostRequestModel postRequestModel) {
        User user = userService.findLoggedInUser();

        Subsection subsection = subsectionRepository.findById(postRequestModel.getSubsectionId()).orElseThrow(() -> new RuntimeException("Subsection not found"));
        Label label = postRequestModel.getLabelId() == null ? null : labelRepository.findById(postRequestModel.getLabelId()).orElseThrow(() -> new RuntimeException("Label not found"));

        Post post = modelMapper.map(postRequestModel, Post.class);
        post.setUserPosted(user);
        post.setSubsection(subsection);
        post.setLabel(label);

        addTags(post);

        postRepository.save(post);

        badgeRewardService.rewardBadge(user, BadgeUnit.TOTAL_POSTS.name());

        PostResponseModel postResponseModel = modelMapper.map(post, PostResponseModel.class);

        return postResponseModel;
    }

    @Override
    public PostResponseModel editPost(UUID postId, PostRequestModel postRequestModel) {
        User user = userService.findLoggedInUser();
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserPosted().getUserId().equals(user.getUserId()) && !user.getRole().getRoleName().equals("ROLE_ADMIN"))
            return null;

        boolean titleAndContentNotChanged = post.getTitle().equals(postRequestModel.getTitle())
                && post.getContent().equals(postRequestModel.getContent());

        PostHistory postHistory = modelMapper.map(post, PostHistory.class);
        postHistory.setPost(post);
        postHistoryRepository.save(postHistory);

        Subsection subsection = subsectionRepository.findById(postRequestModel.getSubsectionId()).orElseThrow(() -> new RuntimeException("Subsection not found"));
        Label label = postRequestModel.getLabelId() == null ? null : labelRepository.findById(postRequestModel.getLabelId()).orElseThrow(() -> new RuntimeException("Label not found"));

        post.setTitle(postRequestModel.getTitle());
        post.setContent(postRequestModel.getContent());
        post.setSubsection(subsection);
        post.setLabel(label);
        post.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        if (!titleAndContentNotChanged) {
            post.getTags().clear();
            addTags(post);
        }

        postRepository.save(post);

        PostResponseModel postResponseModel = modelMapper.map(post, PostResponseModel.class);

        return postResponseModel;
    }

    @Override
    public boolean deletePost(UUID postId) {
        User user = userService.findLoggedInUser();
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserPosted().getUserId().equals(user.getUserId()) && !user.getRole().getRoleName().equals("ROLE_ADMIN"))
            return false;
        else {
            postRepository.delete(post);
            return true;
        }
    }

    public void addTags(Post post) {
        List<String> keywords = tagExtractor.findKeywords(post.getTitle()
                .concat(". ")
                .concat(post.getContent().replace("<[^>]*>", "")));

        for (String keyword : keywords) {
            boolean isExisted = tagRepository.existsByTagName(keyword);
            Tag tag = new Tag();
            if (isExisted) {
                tag = tagRepository.findByTagName(keyword).orElseThrow(() -> new RuntimeException("Tag not found"));
            } else {
                tag.setTagName(keyword);
                tag.setSlug(SlugGenerator.generateSlug(keyword, false));
                tag = tagRepository.save(tag);
            }

            if (!post.getTags().contains(tag)) {
                post.getTags().add(tag);
                postRepository.save(post);
            }
        }
    }

    private PostResponseModel convertToPostModel(Post post) {
        PostResponseModel postResponseModel = modelMapper.map(post, PostResponseModel.class);

        Reply latestReply = post.getReplies()
                .stream()
                .max(Comparator.comparing(Reply::getCreatedAt)).orElse(null);
        BadgeLeanModel badge = badgeService.findBestBadge(post.getUserPosted().getUserId());
        boolean isLabelDisabled = post.getLabel() != null && post.getLabel().isDisabled();
        boolean isSectionDisabled = post.getSubsection() != null && post.getSubsection().getSection() != null && post.getSubsection().getSection().isDisabled();
        boolean isSubsectionDisabled = post.getSubsection() != null && post.getSubsection().isDisabled();
        List<String> peopleLikedImages = post.getPostLikes().stream()
                .map(postLike -> {
                    User userLiked = postLike.getUser();
                    return userLiked.getImage() != null ? userLiked.getImage() : "";
                })
                .collect(Collectors.toList());
        List<String> peopleAcceptedImages = post.getPostAcceptances().stream()
                .map(postAcceptance -> {
                    User userAccepted = postAcceptance.getUser();
                    return userAccepted.getImage() != null ? userAccepted.getImage() : "";
                })
                .collect(Collectors.toList());

        postResponseModel.setTotalLikes(post.getPostLikes().size());
        postResponseModel.setTotalReplies(post.getReplies().size());
        postResponseModel.setLatestReply(
                latestReply == null ? null :
                        modelMapper.map(latestReply, ReplyLeanModel.class));
        postResponseModel.getUserPosted().setBadge(badge);
        postResponseModel.setLabelDisabled(isLabelDisabled);
        postResponseModel.setSectionDisabled(isSectionDisabled);
        postResponseModel.setSubsectionDisabled(isSubsectionDisabled);
        postResponseModel.setPeopleLiked(peopleLikedImages);
        postResponseModel.setPeopleAccepted(peopleAcceptedImages);

        return postResponseModel;
    }

    private DetailPostResponseModel convertToDetailPostModel(Post post) {
        User user = userService.findLoggedInUser();

        DetailPostResponseModel detailPostResponseModel = modelMapper.map(post, DetailPostResponseModel.class);

        boolean isLiked = postLikeRepository.existsByUserAndPost(user, post);
        boolean isMy = post.getUserPosted().getUserId().equals(user.getUserId());
        BadgeLeanModel badge = badgeService.findBestBadge(post.getUserPosted().getUserId());
        boolean isLabelDisabled = post.getLabel() != null && post.getLabel().isDisabled();
        boolean isSectionDisabled = post.getSubsection() != null && post.getSubsection().getSection() != null && post.getSubsection().getSection().isDisabled();
        boolean isSubsectionDisabled = post.getSubsection() != null && post.getSubsection().isDisabled();
        boolean isAccepted = postAcceptanceRepository.findByPostAndUser(post, user).isPresent();
        List<String> peopleLikedImages = post.getPostLikes().stream()
                .map(postLike -> {
                    User userLiked = postLike.getUser();
                    return userLiked.getImage() != null ? userLiked.getImage() : "";
                })
                .collect(Collectors.toList());
        List<String> peopleAcceptedImages = post.getPostAcceptances().stream()
                .map(postAcceptance -> {
                    User userAccepted = postAcceptance.getUser();
                    return userAccepted.getImage() != null ? userAccepted.getImage() : "";
                })
                .collect(Collectors.toList());

        detailPostResponseModel.setLiked(isLiked);
        detailPostResponseModel.setMy(isMy);
        detailPostResponseModel.setAccepted(isAccepted);
        detailPostResponseModel.setTotalLikes(post.getPostLikes().size());
        detailPostResponseModel.setTotalReplies(post.getReplies().size());
        detailPostResponseModel.getUserPosted().setBadge(badge);
        detailPostResponseModel.setLabelDisabled(isLabelDisabled);
        detailPostResponseModel.setSectionDisabled(isSectionDisabled);
        detailPostResponseModel.setSubsectionDisabled(isSubsectionDisabled);
        detailPostResponseModel.setPeopleLiked(peopleLikedImages);
        detailPostResponseModel.setPeopleAccepted(peopleAcceptedImages);

        return detailPostResponseModel;
    }

    //    @PostConstruct
    public void test() {
        List<String> tags = Arrays.asList("ngôn ngữ", "ngôn ngữ tự nhiên", "ai", "ml");
        Post post = postRepository.findById(UUID.fromString("c0a80064-8e78-115a-818e-78e76d6c0004")).get();

        tags.forEach(tag -> {
            if (tagRepository.existsByTagName(tag)) {
                Tag tag1 = tagRepository.findByTagName(tag).orElse(null);

                if (!post.getTags().contains(tag)) {
                    post.getTags().add(tag1);
                    postRepository.save(post);
                }
            }
        });

        List<Post> posts = postRepository.findAll();
        posts.forEach(post1 -> {
            if (post1.getPostHistories().size() == 0)
                post1.setUpdatedAt(null);
            postRepository.save(post1);
        });
    }
}
