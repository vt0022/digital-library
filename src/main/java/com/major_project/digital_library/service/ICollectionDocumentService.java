package com.major_project.digital_library.service;

import com.major_project.digital_library.model.response_model.CollectionResponseModel;

import java.util.UUID;

public interface ICollectionDocumentService {

    CollectionResponseModel addToCollection(UUID collectionId, UUID docId);

    CollectionResponseModel removeFromCollection(UUID collectionId, UUID docId);
}
