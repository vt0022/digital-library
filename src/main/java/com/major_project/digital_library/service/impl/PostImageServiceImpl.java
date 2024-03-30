package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.PostImage;
import com.major_project.digital_library.repository.IPostImageRepository;
import com.major_project.digital_library.service.IPostImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostImageServiceImpl implements IPostImageService {
    private final IPostImageRepository postImageRepository;

    @Autowired
    public PostImageServiceImpl(IPostImageRepository postImageRepository) {
        this.postImageRepository = postImageRepository;
    }

    @Override
    public <S extends PostImage> S save(S entity) {
        return postImageRepository.save(entity);
    }
}
