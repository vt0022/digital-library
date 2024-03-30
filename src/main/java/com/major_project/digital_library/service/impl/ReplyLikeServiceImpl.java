package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IReplyLikeRepository;
import com.major_project.digital_library.service.IReplyLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReplyLikeServiceImpl implements IReplyLikeService {
    private final IReplyLikeRepository replyLikeRepository;

    @Autowired
    public ReplyLikeServiceImpl(IReplyLikeRepository replyLikeRepository) {
        this.replyLikeRepository = replyLikeRepository;
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
}
