package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IReplyLikeRepository;
import com.major_project.digital_library.repository.IReplyRepository;
import com.major_project.digital_library.service.IReplyLikeService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReplyLikeServiceImpl implements IReplyLikeService {
    private final IReplyLikeRepository replyLikeRepository;
    private final IReplyRepository replyRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReplyLikeServiceImpl(IReplyLikeRepository replyLikeRepository, IReplyRepository replyRepository, IUserService userService, ModelMapper modelMapper) {
        this.replyLikeRepository = replyLikeRepository;
        this.replyRepository = replyRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean existsByUserAndReply(User user, Reply reply) {
        return replyLikeRepository.existsByUserAndReply(user, reply);
    }

    @Override
    public <S extends ReplyLike> S save(S entity) {
        return replyLikeRepository.save(entity);
    }

    @Override
    public Optional<ReplyLike> findByUserAndReply(User user, Reply reply) {
        return replyLikeRepository.findByUserAndReply(user, reply);
    }

    @Override
    public void delete(ReplyLike entity) {
        replyLikeRepository.delete(entity);
    }

    @Override
    public boolean likeReply(UUID replyId) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));

        Optional<ReplyLike> replyLike = replyLikeRepository.findByUserAndReply(user, reply);

        if (replyLike.isPresent()) {
            replyLikeRepository.delete(replyLike.get());
            return true;
        } else {
            ReplyLike newReplyLike = new ReplyLike();
            newReplyLike.setUser(user);
            newReplyLike.setReply(reply);
            replyLikeRepository.save(newReplyLike);
            return false;
        }
    }
}
