package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyHistory;
import com.major_project.digital_library.model.response_model.ReplyHistoryResponseModel;
import com.major_project.digital_library.repository.IReplyHistoryRepository;
import com.major_project.digital_library.repository.IReplyRepository;
import com.major_project.digital_library.service.IReplyHistoryService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReplyHistoryServiceImpl implements IReplyHistoryService {
    private final IReplyHistoryRepository replyHistoryRepository;
    private final IReplyRepository replyRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ReplyHistoryServiceImpl(IReplyHistoryRepository replyHistoryRepository, IReplyRepository replyRepository, ModelMapper modelMapper) {
        this.replyHistoryRepository = replyHistoryRepository;
        this.replyRepository = replyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ReplyHistoryResponseModel> findHistoryOfReply(UUID replyId) {
        Reply reply = replyRepository.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));
        List<ReplyHistory> replyHistories = replyHistoryRepository.findAllByReplyOrderByLoggedAtDesc(reply);

        List<ReplyHistoryResponseModel> replyHistoryResponseModels = modelMapper.map(replyHistories, new TypeToken<List<ReplyHistoryResponseModel>>() {
        }.getType());
        return replyHistoryResponseModels;
    }
}
