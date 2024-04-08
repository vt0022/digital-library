package com.major_project.digital_library.service;

import com.major_project.digital_library.model.response_model.ReplyHistoryResponseModel;

import java.util.List;
import java.util.UUID;

public interface IReplyHistoryService {
    List<ReplyHistoryResponseModel> findHistoryOfReply(UUID replyId);
}
