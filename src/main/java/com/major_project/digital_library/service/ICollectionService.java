package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.CollectionRequestModel;
import com.major_project.digital_library.model.response_model.CollectionResponseModel;
import com.major_project.digital_library.model.response_model.DetailCollectionResponseModel;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ICollectionService {
    DetailCollectionResponseModel getDetailCollection(String slug);

    DetailCollectionResponseModel getDetailCollectionForGuest(String slug);

    Page<CollectionResponseModel> getPublicCollections(int page, int size);

    Page<CollectionResponseModel> getCollectionsForUser(int page, int size);

    Page<CollectionResponseModel> getCollectionsOfUser(int page, int size);

    Page<CollectionResponseModel> getCollectionsOfCurrentUser(int page, int size);

    CollectionResponseModel addCollection(CollectionRequestModel collectionRequestModel);

    CollectionResponseModel editCollection(UUID collectionId, CollectionRequestModel collectionRequestModel);

    void removeCollection(UUID collectionId);
}
