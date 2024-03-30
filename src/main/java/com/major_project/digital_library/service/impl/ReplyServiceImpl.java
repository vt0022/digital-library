package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.repository.IReplyRepository;
import com.major_project.digital_library.service.IReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ReplyServiceImpl implements IReplyService {
    private final IReplyRepository replyRepository;

    @Autowired
    public ReplyServiceImpl(IReplyRepository replyRepository) {
        this.replyRepository = replyRepository;
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
    public void deleteById(UUID uuid) {
        replyRepository.deleteById(uuid);
    }
}
