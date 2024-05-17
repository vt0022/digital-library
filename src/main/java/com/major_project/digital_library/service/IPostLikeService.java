package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.PostLikeResponseModel;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface IPostLikeService {
    boolean existsByUserAndPost(User user, Post post);

    <S extends PostLike> S save(S entity);

    void deleteByUserAndPost(User user, Post post);

    void delete(PostLike entity);

    Optional<PostLike> findByUserAndPost(User user, Post post);

    boolean likePost(UUID postId);

    Page<PostLikeResponseModel> findByUser(int page, int size);
}
