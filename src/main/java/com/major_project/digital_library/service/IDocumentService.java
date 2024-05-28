package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.DocumentRequestModel;
import com.major_project.digital_library.model.response_model.DetailDocumentResponseModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface IDocumentService {
    DetailDocumentResponseModel viewDocument(String slug);

    DetailDocumentResponseModel viewDocumentForGuest(String slug);

    Page<DocumentResponseModel> getAllDocuments(int page, int size, String order, String category, String field, String organization, String deleted, String internal, String status, String s);

    Page<DocumentResponseModel> getMyUploads(int page, int size, String order, String category, String organization, String field, String status, String s);

    Page<DocumentResponseModel> getOwnedDocuments(int page, int size);

    Page<DocumentResponseModel> getDocumentsByUser(UUID userId, int page, int size);

    Page<DocumentResponseModel> findDocumentsByUserForStudent(UUID userId, int page, int size, String order, String category, String field, String s);

    Page<DocumentResponseModel> findDocumentsByUserForGuest(UUID userId, int page, int size, String order, String category, String field, String s);

    Page<DocumentResponseModel> getDocumentsForGuests(int page, int size, String order, String sortOrder, String category, String field, String organization, String s);

    Page<DocumentResponseModel> getPendingDocuments(int page, int size, String status, String organization);

    Page<DocumentResponseModel> getDocumentsForStudent(int page, int size, String order, String sortOrder, String category, String field, String organization, String s);

    DocumentResponseModel uploadDocument(DocumentRequestModel documentRequestModel,
                                         MultipartFile multipartFile);

    DocumentResponseModel updateDocument(String slug, DocumentRequestModel documentRequestModel,
                                         MultipartFile multipartFile);

    void deleteDocument(UUID docId);

    void approveDocument(UUID docId, boolean isApproved, String note);

    Page<DocumentResponseModel> getLatestDocuments(int page, int size, String order, String category, String field, String organization, String deleted, String internal, String status, String s);

    Page<DocumentResponseModel> findRelatedDocuments(String query);
}
