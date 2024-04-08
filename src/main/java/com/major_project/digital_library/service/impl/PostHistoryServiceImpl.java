package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostHistory;
import com.major_project.digital_library.model.response_model.PostHistoryResponseModel;
import com.major_project.digital_library.repository.IPostHistoryRepository;
import com.major_project.digital_library.repository.IPostRepository;
import com.major_project.digital_library.service.IPostHistoryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PostHistoryServiceImpl implements IPostHistoryService {
    private final IPostRepository postRepository;
    private final IPostHistoryRepository postHistoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PostHistoryServiceImpl(IPostRepository postRepository, IPostHistoryRepository postHistoryRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.postHistoryRepository = postHistoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<PostHistoryResponseModel> findHistoryOfPost(UUID postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        List<PostHistory> postHistories = postHistoryRepository.findAllByPostOrderByLoggedAtDesc(post);

        List<PostHistoryResponseModel> postHistoryResponseModels = modelMapper.map(postHistories, new TypeToken<List<PostHistoryResponseModel>>() {
        }.getType());
        return postHistoryResponseModels;
    }
}
