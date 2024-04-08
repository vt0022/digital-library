package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyHistory;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.ReplyRequestModel;
import com.major_project.digital_library.model.response_model.ReplyResponseModel;
import com.major_project.digital_library.repository.IPostRepository;
import com.major_project.digital_library.repository.IReplyHistoryRepository;
import com.major_project.digital_library.repository.IReplyLikeRepository;
import com.major_project.digital_library.repository.IReplyRepository;
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
    private final ModelMapper modelMapper;

    @Autowired
    public ReplyServiceImpl(IReplyRepository replyRepository, IUserService userService, IReplyLikeRepository replyLikeRepository, IPostRepository postRepository, IReplyHistoryRepository replyHistoryRepository, ModelMapper modelMapper) {
        this.replyRepository = replyRepository;
        this.userService = userService;
        this.replyLikeRepository = replyLikeRepository;
        this.postRepository = postRepository;
        this.replyHistoryRepository = replyHistoryRepository;
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
        Page<Reply> replies = replyRepository.findAllByPostOrderByCreatedAtAsc(post, pageable);

        Page<ReplyResponseModel> replyResponseModels = replies.map(this::convertToReplyModelForGuest);
        return replyResponseModels;
    }

    @Override
    public Page<ReplyResponseModel> getRepliesOfPost(UUID postId, int page, int size) {
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

        replyRepository.save(reply);

        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);
        return replyResponseModel;
    }

    @Override
    public ReplyResponseModel editReply(UUID replyId, Map<String, String> replyContent) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));

        if (!reply.getUser().getUserId().equals(user.getUserId()))
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
    public void deleteReply(UUID replyId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));

        replyRepository.delete(reply);
    }

    @Override
    public Page<ReplyResponseModel> getRepliesOfUser(UUID userId, int page, int size) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Reply> replies = replyRepository.findAllByUserOrderByCreatedAtDesc(user, pageable);

        Page<ReplyResponseModel> replyResponseModels = replies.map(this::convertToReplyModelForGuest);

        return replyResponseModels;
    }

    private ReplyResponseModel convertToReplyModelForGuest(Reply reply) {
        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);
        replyResponseModel.setTotalLikes(reply.getReplyLikes().size());
        return replyResponseModel;
    }

    private ReplyResponseModel convertToReplyModel(Reply reply) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
        boolean isLiked = replyLikeRepository.existsByUserAndReply(user, reply);
        boolean isMy = reply.getUser().getUserId().equals(user.getUserId());

        ReplyResponseModel replyResponseModel = modelMapper.map(reply, ReplyResponseModel.class);
        replyResponseModel.setLiked(isLiked);
        replyResponseModel.setMy(isMy);
        replyResponseModel.setTotalLikes(reply.getReplyLikes().size());

        return replyResponseModel;
    }
}
