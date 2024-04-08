package com.major_project.digital_library.service;

import com.major_project.digital_library.model.response_model.PostHistoryResponseModel;

import java.util.List;
import java.util.UUID;

public interface IPostHistoryService {
    List<PostHistoryResponseModel> findHistoryOfPost(UUID postId);
}
