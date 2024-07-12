package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.PostReportRequestModel;
import com.major_project.digital_library.model.response_model.PostReportResponseModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IPostReportService {
    Page<PostReportResponseModel> findAllReports(int page, int size, String type, String status);

    PostReportResponseModel reportPost(PostReportRequestModel postReportRequestModel);

    PostReportResponseModel readReport(UUID postReportId);

    boolean handleReport(UUID reportId, String type, String action);

    List<PostReportResponseModel> checkReport(UUID reportId);

    void deleteReport(UUID postReportId);
}
