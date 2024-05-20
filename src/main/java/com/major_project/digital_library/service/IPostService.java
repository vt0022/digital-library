package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.PostRequestModel;
import com.major_project.digital_library.model.response_model.DetailPostResponseModel;
import com.major_project.digital_library.model.response_model.PostResponseModel;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IPostService {

    DetailPostResponseModel getPostDetail(UUID postId);

    PostResponseModel getPostDetailForGuest(UUID postId);

    Page<PostResponseModel> findViewablePosts(int page, int size, String order, String subsectionSlug, String labelSlug, String query);

    Page<PostResponseModel> findAllPosts(int page, int size, String order, String subsectionSlug, String labelSlug, String query);

    Page<PostResponseModel> findViewablePostsOfUser(UUID userId, int page, int size, String query);

    Page<PostResponseModel> findAllPostsOfUser(UUID userId, int page, int size, String query);

    Page<PostResponseModel> findRelatedPosts(String query);

    PostResponseModel addPost(PostRequestModel postRequestModel);

    PostResponseModel editPost(UUID postId, PostRequestModel postRequestModel);

    boolean deletePost(UUID postId);
}
