package com.major_project.digital_library.service;

import com.major_project.digital_library.model.response_model.LabelResponseModel;

import java.util.List;

public interface ILabelService {
    List<LabelResponseModel> findActiveLabels();
}
