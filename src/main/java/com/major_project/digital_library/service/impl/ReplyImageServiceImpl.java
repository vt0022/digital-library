package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.ReplyImage;
import com.major_project.digital_library.repository.IReplyImageRepository;
import com.major_project.digital_library.service.IReplyImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplyImageServiceImpl implements IReplyImageService {
    private final IReplyImageRepository replyImageRepository;

    @Autowired
    public ReplyImageServiceImpl(IReplyImageRepository replyImageRepository) {
        this.replyImageRepository = replyImageRepository;
    }

    @Override
    public <S extends ReplyImage> S save(S entity) {
        return replyImageRepository.save(entity);
    }
}
