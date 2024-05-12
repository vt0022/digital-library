package com.major_project.digital_library.service;

import com.major_project.digital_library.model.response_model.DetailSectionResponseModel;
import com.major_project.digital_library.model.response_model.SectionResponseModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ISectionService {
    List<DetailSectionResponseModel> findActiveSections();

    Page<SectionResponseModel> findAllSections(String disabled, String s, int page, int size);
}
