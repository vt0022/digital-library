package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.ReplyReportRequestModel;
import com.major_project.digital_library.model.response_model.ReplyReportResponseModel;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IReplyReportService {
    Page<ReplyReportResponseModel> findAllReports(int page, int size, String type, String read);

    ReplyReportResponseModel reportReply(ReplyReportRequestModel replyReportRequestModel);

    ReplyReportResponseModel readReport(UUID replyReportId);

    boolean handleReport(UUID reportId, String type);

    void deleteReport(UUID replyReportId);
}
