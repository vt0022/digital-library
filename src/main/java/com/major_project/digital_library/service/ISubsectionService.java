package com.major_project.digital_library.service;

import com.major_project.digital_library.model.response_model.SubsectionResponseModel;

import java.util.List;

public interface ISubsectionService {
    List<SubsectionResponseModel> findEditableSubsections();
}
