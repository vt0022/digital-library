package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.SectionRequestModel;
import com.major_project.digital_library.model.response_model.DetailSectionResponseModel;
import com.major_project.digital_library.model.response_model.SectionResponseModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ISectionService {
    List<DetailSectionResponseModel> findActiveSections();

    Page<SectionResponseModel> findAllSections(String disabled, String s, int page, int size);

    SectionResponseModel findSection(UUID subId);

    SectionResponseModel createSection(SectionRequestModel sectionRequestModel);

    SectionResponseModel updateSection(UUID subId, SectionRequestModel sectionRequestModel);

    boolean deleteSection(UUID subId);

    SectionResponseModel activateSection(UUID subId);
}
