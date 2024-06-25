package com.major_project.digital_library.service;

import com.major_project.digital_library.model.response_model.PostLikeResponseModel;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IPostLikeService {
    boolean likePost(UUID postId);

    Page<PostLikeResponseModel> findByUser(int page, int size);
}
