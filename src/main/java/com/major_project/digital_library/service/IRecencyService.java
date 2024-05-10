package com.major_project.digital_library.service;

import com.major_project.digital_library.model.response_model.DocumentResponseModel;

import java.util.List;

public interface IRecencyService {

    List<DocumentResponseModel> getRecentDocuments();

    void addToRecentDocuments(String slug);
}
