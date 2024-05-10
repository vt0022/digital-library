package com.major_project.digital_library.service;

import com.major_project.digital_library.model.SaveModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import org.springframework.data.domain.Page;

public interface ISaveService {

    void saveDocument(String slug);

    SaveModel unsaveDocument(String slug);

    void undoUnsave(String slug, SaveModel saveModel);

    Page<DocumentResponseModel> getSavedDocuments(int page, int size, String s);
}
