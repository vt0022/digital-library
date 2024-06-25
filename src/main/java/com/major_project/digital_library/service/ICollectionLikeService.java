package com.major_project.digital_library.service;

import com.major_project.digital_library.model.CollectionLikeModel;
import com.major_project.digital_library.model.response_model.CollectionResponseModel;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface ICollectionLikeService {

    void likeCollection(UUID collectionId);

    CollectionLikeModel unlikeCollection(UUID collectionId);

    void undoUnlike(UUID collectionId, CollectionLikeModel collectionLikeModel);

    Page<CollectionResponseModel> getLikedCollections(int page, int size, String s);
}
