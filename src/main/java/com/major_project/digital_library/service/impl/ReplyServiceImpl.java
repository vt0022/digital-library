package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.NotificationMessage;
import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyHistory;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.lean_model.BadgeLeanModel;
import com.major_project.digital_library.model.request_model.ReplyRequestModel;
import com.major_project.digital_library.model.response_model.ReplyResponseModel;
import com.major_project.digital_library.repository.IPostRepository;
import com.major_project.digital_library.repository.IReplyHistoryRepository;
import com.major_project.digital_library.repository.IReplyLikeRepository;
import com.major_project.digital_library.repository.IReplyRepository;
import com.major_project.digital_library.service.IBadgeService;
import com.major_project.digital_library.service.INotificationService;
import com.major_project.digital_library.service.IReplyService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReplyServiceImpl implements IReplyService {
    private final IReplyRepository replyRepository;
    private final IReplyLikeRepository replyLikeRepository;
    private final IPostRepository postRepository;
    private final IReplyHistoryRepository replyHistoryRepository;
    private final IUserService userService;
    private final IBadgeService badgeService;
    private final INotificationService notificationService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReplyServiceImpl(IReplyRepository replyRepository, IUserService userService, IReplyLikeRepository replyLikeRepository, IPostRepository postRepository, IReplyHistoryRepository replyHistoryRepository, IBadgeService badgeService, INotificationService notificationService, ModelMapper modelMapper) {
        this.replyRepository = replyRepository;
        this.userService = userService;
        this.replyLikeRepository = replyLikeRepository;
        this.postRepository = postRepository;
        this.replyHistoryRepository = replyHistoryRepository;
        this.badgeService = badgeService;
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<Reply> findAllByPostOrderByCreatedAtAsc(Post post, Pageable pageable) {
        return replyRepository.findAllByPostOrderByCreatedAtAsc(post, pageable);
    }

    @Override
    public <S extends Reply> S save(S entity) {
        return replyRepository.save(entity);
    }

    @Override
    public Optional<Reply> findById(UUID uuid) {
        return replyRepository.findById(uuid);
    }

    @Override
    public Page<Reply> findAllByUserOrderByCreatedAtDesc(User user, Pageable pageable) {
        return replyRepository.findAllByUserOrderByCreatedAtDesc(user, pageable);
    }

    @Override
    public void deleteById(UUID uuid) {
        replyRepository.deleteById(uuid);
    }

    @Override
    public Page<ReplyResponseModel> getRepliesOfPostForGuest(UUID postId, int page, int size) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reply> replies = replyRepository.findViewableRepliesByPost(post, pageable);

        Page<ReplyResponseModel> replyResponseModels = replies.map(this::convertToReplyModelForGuest);
        return replyResponseModels;
    }

    @Override
    public Page<ReplyResponseModel> getViewableRepliesOfPost(UUID postId, int page, int size) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reply> replies = replyRepository.findViewableRepliesByPost(post, pageable);

        Page<ReplyResponseModel> replyResponseModels = replies.map(this::convertToReplyModel);
        return replyResponseModels;
    }

    @Override
    public Page<ReplyResponseModel> getAllRepliesOfPost(UUID postId, int page, int size) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reply> replies = replyRepository.findAllByPostOrderByCreatedAtAsc(post, pageable);

        Page<ReplyResponseModel> replyResponseModels = replies.map(this::convertToReplyModel);
        return replyResponseModels;
    }

    @Override
    public ReplyResponseModel addReply(UUID postId, ReplyRequestModel replyRequestModel) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

        Reply parentReply = replyRequestModel.getParentReplyId() == null ? null : replyRepository.findById(replyRequestModel.getParentReplyId()).orElse(null);

        Reply reply = new Reply();
        reply.setContent(replyRequestModel.getContent());
        reply.setParentReply(parentReply);
        reply.setUser(user);
        reply.setPost(post);

        reply = replyRepository.save(reply);

        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);

        if (!post.getUserPosted().getUserId().equals(user.getUserId()))
            notificationService.sendNotification(NotificationMessage.REPLY.name(), NotificationMessage.REPLY.getMessage(), user, post.getUserPosted(), reply);

        return replyResponseModel;
    }

    @Override
    public ReplyResponseModel editReply(UUID replyId, Map<String, String> replyContent) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getUser().getUserId().equals(user.getUserId()) && !user.getRole().getRoleName().equals("ROLE_ADMIN"))
            return null;

        ReplyHistory replyHistory = modelMapper.map(reply, ReplyHistory.class);
        replyHistory.setReply(reply);
        replyHistoryRepository.save(replyHistory);

        reply.setContent(replyContent.get("content"));
        replyRepository.save(reply);

        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);
        return replyResponseModel;
    }

    @Override
    public boolean deleteReply(UUID replyId) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getUser().getUserId().equals(user.getUserId()) && !user.getRole().getRoleName().equals("ROLE_ADMIN"))
            return false;
        else {
            replyRepository.delete(reply);
            return true;
        }
    }

    @Override
    public Page<ReplyResponseModel> getViewableRepliesOfUser(UUID userId, int page, int size) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reply> replies = replyRepository.findViewableRepliesByUser(user, pageable);

        Page<ReplyResponseModel> replyResponseModels = replies.map(this::convertToReplyModelForGuest);

        return replyResponseModels;
    }

    @Override
    public Page<ReplyResponseModel> getAllRepliesOfUser(UUID userId, int page, int size) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reply> replies = replyRepository.findAllByUserOrderByCreatedAtDesc(user, pageable);

        Page<ReplyResponseModel> replyResponseModels = replies.map(this::convertToReplyModelForGuest);

        return replyResponseModels;
    }

    private ReplyResponseModel convertToReplyModelForGuest(Reply reply) {
        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);

        BadgeLeanModel badge = badgeService.findBestBadge(reply.getUser().getUserId());
        boolean isPostDisabled = checkPostDisabled(reply);

        replyResponseModel.setTotalLikes(reply.getReplyLikes().size());
        replyResponseModel.getUser().setBadge(badge);
        replyResponseModel.setPostDisabled(isPostDisabled);

        return replyResponseModel;
    }

    private ReplyResponseModel convertToReplyModel(Reply reply) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
        boolean isLiked = replyLikeRepository.existsByUserAndReply(user, reply);
        boolean isMy = reply.getUser().getUserId().equals(user.getUserId());
        BadgeLeanModel badge = badgeService.findBestBadge(reply.getUser().getUserId());
        boolean isPostDisabled = checkPostDisabled(reply);

        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);
        replyResponseModel.setLiked(isLiked);
        replyResponseModel.setMy(isMy);
        replyResponseModel.setTotalLikes(reply.getReplyLikes().size());
        replyResponseModel.getUser().setBadge(badge);
        replyResponseModel.setPostDisabled(isPostDisabled);

        return replyResponseModel;
    }

    private boolean checkPostDisabled(Reply reply) {
        Post post = reply.getPost();

        boolean isLabelDisabled = post.getLabel() != null && post.getLabel().isDisabled();
        boolean isSectionDisabled = post.getSubsection() != null && post.getSubsection().getSection() != null && post.getSubsection().getSection().isDisabled();
        boolean isSubsectionDisabled = post.getSubsection() != null && post.getSubsection().isDisabled();

        return post.isDisabled() || isLabelDisabled || isSubsectionDisabled || isSectionDisabled;
    }
}
