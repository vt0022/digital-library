package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.CollectionLikeModel;
import com.major_project.digital_library.model.response_model.CollectionResponseModel;
import com.major_project.digital_library.repository.ICollectionLikeRepository;
import com.major_project.digital_library.repository.ICollectionRepository;
import com.major_project.digital_library.service.ICollectionLikeService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CollectionLikeServiceImpl implements ICollectionLikeService {
    private final ICollectionLikeRepository collectionLikeRepository;
    private final ICollectionRepository collectionRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public CollectionLikeServiceImpl(ICollectionLikeRepository collectionLikeRepository, ICollectionRepository collectionRepository, IUserService userService, ModelMapper modelMapper) {
        this.collectionLikeRepository = collectionLikeRepository;
        this.collectionRepository = collectionRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void likeCollection(UUID collectionId) {
        User user = userService.findLoggedInUser();
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new RuntimeException("Collection not found"));

        Optional<CollectionLike> collectionLike = collectionLikeRepository.findByUserAndCollection(user, collection);

        if (!collectionLike.isPresent()) {
            CollectionLike newCollectionLike = new CollectionLike();
            newCollectionLike.setUser(user);
            newCollectionLike.setCollection(collection);
            collectionLikeRepository.save(newCollectionLike);
        }
    }

    @Override
    public CollectionLikeModel unlikeCollection(UUID collectionId) {
        User user = userService.findLoggedInUser();
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new RuntimeException("Collection not found"));

        Optional<CollectionLike> collectionLike = collectionLikeRepository.findByUserAndCollection(user, collection);

        if (collectionLike.isPresent()) {
            CollectionLikeModel collectionLikeModel = CollectionLikeModel.builder()
                    .collectionId(collectionLike.get().getCollection().getCollectionId())
                    .likedAt(collectionLike.get().getLikedAt())
                    .build();

            collectionLikeRepository.delete(collectionLike.get());

            return collectionLikeModel;
        }

        return null;
    }

    @Override
    public void undoUnlike(UUID collectionId, CollectionLikeModel collectionLikeModel) {
        User user = userService.findLoggedInUser();
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new RuntimeException("Collection not found"));

        Optional<CollectionLike> collectionLikeOptional = collectionLikeRepository.findByUserAndCollection(user, collection);
        if (!collectionLikeOptional.isPresent() && collectionId.equals(collectionLikeModel.getCollectionId())) {
            CollectionLike collectionLike = new CollectionLike();
            collectionLike.setCollection(collection);
            collectionLike.setUser(user);
            collectionLike.setLikedAt(collectionLikeModel.getLikedAt());

            collectionLikeRepository.save(collectionLike);
        }
    }

    @Override
    public Page<CollectionResponseModel> getLikedCollections(int page, int size, String s) {
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(page, size);
        Page<Collection> collections = collectionLikeRepository.findLikedCollections(user, s, pageable);

        Page<CollectionResponseModel> collectionResponseModels = collections.map(this::convertToCollectionModel);

        return collectionResponseModels;
    }

    public CollectionResponseModel convertToCollectionModel(Collection collection) {
        CollectionResponseModel collectionResponseModel = modelMapper.map(collection, CollectionResponseModel.class);

        List<Document> documents = collection.getCollectionDocuments().stream()
                .map(CollectionDocument::getDocument)
                .collect(Collectors.toList());

        List<String> thumbnails = new ArrayList<>();

        if (documents.size() >= 1)
            thumbnails.add(documents.get(0).getThumbnail());
        if (documents.size() >= 2)
            thumbnails.add(documents.get(1).getThumbnail());
        if (documents.size() > 2)
            thumbnails.add(documents.get(2).getThumbnail());

        collectionResponseModel.setThumbnails(thumbnails);

        return collectionResponseModel;
    }
}
