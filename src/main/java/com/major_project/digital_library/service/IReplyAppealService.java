package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.ReplyAppealRequestModel;
import com.major_project.digital_library.model.response_model.ReplyAppealResponseModel;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IReplyAppealService {
    Page<ReplyAppealResponseModel> findAllAppeals(int page, int size, String type, String status);

    ReplyAppealResponseModel appealReply(ReplyAppealRequestModel replyAppealRequestModel);

    ReplyAppealResponseModel readAppeal(UUID replyAppealId);

    boolean handleAppeal(UUID appealId, String type);

    void deleteAppeal(UUID replyAppealId);

    ReplyAppealResponseModel checkAppeal(UUID replyReportId);
}
