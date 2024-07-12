package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.PostAppealRequestModel;
import com.major_project.digital_library.model.response_model.PostAppealResponseModel;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface IPostAppealService {
    Page<PostAppealResponseModel> findAllAppeals(int page, int size, String type, String status);

    PostAppealResponseModel appealPost(PostAppealRequestModel postAppealRequestModel);

    PostAppealResponseModel readAppeal(UUID postAppealId);

    boolean handleAppeal(UUID appealId, String action);

    void deleteAppeal(UUID postAppealId);

    PostAppealResponseModel checkAppeal(UUID postReportId);
}
