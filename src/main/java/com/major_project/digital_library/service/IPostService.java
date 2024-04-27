package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.model.request_model.PostRequestModel;
import com.major_project.digital_library.model.response_model.DetailPostResponseModel;
import com.major_project.digital_library.model.response_model.PostResponseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface IPostService {
    <S extends Post> S save(S entity);

    Optional<Post> findById(UUID uuid);

    void deleteById(UUID uuid);

    Page<Post> findAll(Pageable pageable);

    DetailPostResponseModel getPostDetail(UUID postId);

    PostResponseModel getPostDetailForGuest(UUID postId);

    Page<PostResponseModel> findPosts(int page, int size, String order, String subsection, String label, String query);

    Page<PostResponseModel> findPostsOfUser(UUID userId, int page, int size, String query);

    Page<PostResponseModel> findRelatedPosts(String query);

    PostResponseModel addPost(PostRequestModel postRequestModel);

    PostResponseModel editPost(UUID postId, PostRequestModel postRequestModel);

    void deletePost(UUID postId);
}
