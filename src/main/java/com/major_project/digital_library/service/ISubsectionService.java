package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.SubsectionRequestModel;
import com.major_project.digital_library.model.response_model.SubsectionResponseModel;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface ISubsectionService {
    List<SubsectionResponseModel> findEditableSubsections();

    Page<SubsectionResponseModel> findAllSubsections(String disabled, String editable, String s, int page, int size);

    SubsectionResponseModel findSubsection(UUID subId);

    SubsectionResponseModel createSubsection(SubsectionRequestModel subsectionRequestModel);

    SubsectionResponseModel updateSubsection(UUID subId,
                                             SubsectionRequestModel subsectionRequestModel);

    boolean deleteSubsection(UUID subId);

    SubsectionResponseModel activateSubsection(UUID subId);
}
