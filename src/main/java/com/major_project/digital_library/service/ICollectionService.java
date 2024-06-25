package com.major_project.digital_library.service;

import com.major_project.digital_library.model.request_model.CollectionRequestModel;
import com.major_project.digital_library.model.response_model.CollectionResponseModel;
import com.major_project.digital_library.model.response_model.DetailCollectionResponseModel;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ICollectionService {
    DetailCollectionResponseModel getDetailCollection(String slug, String s);

    DetailCollectionResponseModel getDetailCollectionForGuest(String slug, String s);

    Page<CollectionResponseModel> getPublicCollections(int page, int size, String s);

    Page<CollectionResponseModel> getCollectionsForUser(int page, int size, String s);

    Page<CollectionResponseModel> getCollectionsOfUser(int page, int size, String s);

    CollectionResponseModel addCollection(CollectionRequestModel collectionRequestModel);

    CollectionResponseModel editCollection(UUID collectionId, CollectionRequestModel collectionRequestModel);

    void removeCollection(UUID collectionId);
}
