package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.DocumentNoteRequestModel;
import com.major_project.digital_library.model.response_model.DocumentNoteResponseModel;
import org.springframework.data.domain.Page;

public interface IDocumentNoteService {
    DocumentNoteResponseModel saveNote(String slug, DocumentNoteRequestModel documentNoteRequestModel);

    void deleteNote(String slug, int page);

    DocumentNoteResponseModel getNote(String slug, int page);

    Page<DocumentNoteResponseModel> getAllNotesOfDocument(String slug);
}
