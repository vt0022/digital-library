package com.major_project.digital_library.service;

import com.major_project.digital_library.model.response_model.SectionResponseModel;

import java.util.List;

public interface ISectionService {
    List<SectionResponseModel> findActiveSections();
}
