package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.repository.IPostLikeRepository;
import com.major_project.digital_library.service.IPostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostLikeServiceImpl implements IPostLikeService {
    private final IPostLikeRepository postLikeRepository;

    @Autowired
    public PostLikeServiceImpl(IPostLikeRepository postLikeRepository) {
        this.postLikeRepository = postLikeRepository;
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
}
