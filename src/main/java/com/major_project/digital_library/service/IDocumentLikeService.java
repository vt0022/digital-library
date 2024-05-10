package com.major_project.digital_library.service;

import com.major_project.digital_library.model.DocumentLikeModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import org.springframework.data.domain.Page;

public interface IDocumentLikeService {

    void likeDocument(String slug);

    DocumentLikeModel unlikeDocument(String slug);

    void undoUnlike(String slug, DocumentLikeModel documentLikeModel);

    Page<DocumentResponseModel> getLikedDocuments(int page, int size, String s);
}
