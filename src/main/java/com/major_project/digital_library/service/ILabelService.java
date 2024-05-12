package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.LabelRequestModel;
import com.major_project.digital_library.model.response_model.LabelResponseModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ILabelService {
    List<LabelResponseModel> findActiveLabels();

    Page<LabelResponseModel> findAllLabels(String disabled, String s, int page, int size);

    LabelResponseModel findLabel(UUID subId);

    LabelResponseModel createLabel(LabelRequestModel labelRequestModel);

    LabelResponseModel updateLabel(UUID subId,
                                   LabelRequestModel labelRequestModel);

    boolean deleteLabel(UUID subId);

    LabelResponseModel activateLabel(UUID subId);
}
