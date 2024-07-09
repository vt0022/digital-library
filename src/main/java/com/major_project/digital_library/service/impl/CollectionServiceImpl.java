package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Collection;
import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.CollectionRequestModel;
import com.major_project.digital_library.model.response_model.CollectionResponseModel;
import com.major_project.digital_library.model.response_model.DetailCollectionResponseModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.repository.ICollectionDocumentRepository;
import com.major_project.digital_library.repository.ICollectionLikeRepository;
import com.major_project.digital_library.repository.ICollectionRepository;
import com.major_project.digital_library.repository.IDocumentRepository;
import com.major_project.digital_library.service.ICollectionService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.util.SlugGenerator;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CollectionServiceImpl implements ICollectionService {
    private final ICollectionRepository collectionRepository;
    private final IDocumentRepository documentRepository;
    private final ICollectionDocumentRepository collectionDocumentRepository;
    private final ICollectionLikeRepository collectionLikeRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public CollectionServiceImpl(ICollectionRepository collectionRepository, IDocumentRepository documentRepository, ICollectionDocumentRepository collectionDocumentRepository, ICollectionLikeRepository collectionLikeRepository, IUserService userService, ModelMapper modelMapper) {
        this.collectionRepository = collectionRepository;
        this.documentRepository = documentRepository;
        this.collectionDocumentRepository = collectionDocumentRepository;
        this.collectionLikeRepository = collectionLikeRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public DetailCollectionResponseModel getDetailCollection(String slug, String s) {
        Collection collection = collectionRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Collection not found"));

        User user = userService.findLoggedInUser();

        if (collection.isPrivate() && !collection.getUser().getUserId().equals(user.getUserId()))
            return null;

        DetailCollectionResponseModel detailCollectionResponseModel = convertToDetailCollectionModel(collection, s);

        return detailCollectionResponseModel;
    }

    @Override
    public DetailCollectionResponseModel getDetailCollectionForGuest(String slug, String s) {
        Collection collection = collectionRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Collection not found"));

        if (collection.isPrivate())
            return null;

        DetailCollectionResponseModel detailCollectionResponseModel = convertToDetailCollectionModelForGuest(collection, s);

        return detailCollectionResponseModel;
    }

    @Override
    public Page<CollectionResponseModel> getPublicCollections(int page, int size, String s) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Collection> collections = collectionRepository.findPublicCollections(s, pageable);
        Page<CollectionResponseModel> collectionResponseModels = collections.map(this::convertToCollectionModelForGuest);

        return collectionResponseModels;
    }

    @Override
    public Page<CollectionResponseModel> getCollectionsForUser(int page, int size, String s) {
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Collection> collections = collectionRepository.findForUser(user, s, pageable);
        Page<CollectionResponseModel> collectionResponseModels = collections.map(this::convertToCollectionModelForUser);

        return collectionResponseModels;

    }

    @Override
    public Page<CollectionResponseModel> getCollectionsOfUser(int page, int size, String s) {
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Collection> collections = collectionRepository.findByUser(user, s, pageable);
        Page<CollectionResponseModel> collectionResponseModels = collections.map(this::convertToCollectionModelForUser);

        return collectionResponseModels;

    }

    @Override
    public CollectionResponseModel addCollection(CollectionRequestModel collectionRequestModel) {
        User user = userService.findLoggedInUser();

        Collection collection = modelMapper.map(collectionRequestModel, Collection.class);
        collection.setUser(user);
        collection.setSlug(SlugGenerator.generateSlug(collectionRequestModel.getCollectionName(), true));

        collection = collectionRepository.save(collection);

        CollectionResponseModel collectionResponseModel = modelMapper.map(collection, CollectionResponseModel.class);

        return collectionResponseModel;
    }

    @Override
    public CollectionResponseModel editCollection(UUID collectionId, CollectionRequestModel collectionRequestModel) {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new RuntimeException("Collection not found"));
        collection.setCollectionName(collectionRequestModel.getCollectionName());
        collection.setPrivate(collectionRequestModel.isPrivate());
        collection.setSlug(SlugGenerator.generateSlug(collectionRequestModel.getCollectionName(), true));

        collection = collectionRepository.save(collection);

        CollectionResponseModel collectionResponseModel = modelMapper.map(collection, CollectionResponseModel.class);

        return collectionResponseModel;
    }

    @Override
    public void removeCollection(UUID collectionId) {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new RuntimeException("Collection not found"));

        collectionRepository.delete(collection);
    }

    public CollectionResponseModel convertToCollectionModelForUser(Collection collection) {
        User user = userService.findLoggedInUser();
        CollectionResponseModel collectionResponseModel = modelMapper.map(collection, CollectionResponseModel.class);

        Pageable pageable = PageRequest.of(0, 100);
        List<Document> documents = documentRepository.findByCollectionForUser(collection, user.getOrganization(), "", pageable).getContent();
        int totalDocuments = documents.size();
        int totalLikes = collection.getCollectionLikes().size();

        List<String> thumbnails = new ArrayList<>();
        if (documents.size() >= 1)
            thumbnails.add(documents.get(0).getThumbnail());
        if (documents.size() >= 2)
            thumbnails.add(documents.get(1).getThumbnail());
        if (documents.size() > 2)
            thumbnails.add(documents.get(2).getThumbnail());

        collectionResponseModel.setThumbnails(thumbnails);
        collectionResponseModel.setTotalDocuments(totalDocuments);
        collectionResponseModel.setTotalLikes(totalLikes);

        return collectionResponseModel;
    }

    public CollectionResponseModel convertToCollectionModelForGuest(Collection collection) {
        CollectionResponseModel collectionResponseModel = modelMapper.map(collection, CollectionResponseModel.class);

        Pageable pageable = PageRequest.of(0, 1000);
        List<Document> documents = documentRepository.findByCollectionForGuest(collection, "", pageable).getContent();
        int totalDocuments = documents.size();
        int totalLikes = collection.getCollectionLikes().size();

        List<String> thumbnails = new ArrayList<>();
        if (documents.size() >= 1)
            thumbnails.add(documents.get(0).getThumbnail());
        if (documents.size() >= 2)
            thumbnails.add(documents.get(1).getThumbnail());
        if (documents.size() > 2)
            thumbnails.add(documents.get(2).getThumbnail());

        collectionResponseModel.setThumbnails(thumbnails);
        collectionResponseModel.setTotalDocuments(totalDocuments);
        collectionResponseModel.setTotalLikes(totalLikes);

        return collectionResponseModel;
    }

    public DetailCollectionResponseModel convertToDetailCollectionModel(Collection collection, String s) {
        User user = userService.findLoggedInUser();

        DetailCollectionResponseModel detailCollectionResponseModel = modelMapper.map(collection, DetailCollectionResponseModel.class);

        Pageable pageable = PageRequest.of(0, 100);
        List<Document> documents = documentRepository.findByCollectionForUser(collection, user.getOrganization(), s, pageable).getContent();
        List<DocumentResponseModel> documentResponseModels = modelMapper.map(documents, new TypeToken<List<DocumentResponseModel>>() {
        }.getType());
        int totalDocuments = documents.size();
        boolean isMine = user.getUserId().equals(collection.getUser().getUserId());
        boolean isLiked = collectionLikeRepository.findByUserAndCollection(user, collection).isPresent();
        int totalLikes = collection.getCollectionLikes().size();

        detailCollectionResponseModel.setTotalDocuments(totalDocuments);
        detailCollectionResponseModel.setDocuments(documentResponseModels);
        detailCollectionResponseModel.setMine(isMine);
        detailCollectionResponseModel.setLiked(isLiked);
        detailCollectionResponseModel.setTotalLikes(totalLikes);

        return detailCollectionResponseModel;
    }

    public DetailCollectionResponseModel convertToDetailCollectionModelForGuest(Collection collection, String s) {
        DetailCollectionResponseModel detailCollectionResponseModel = modelMapper.map(collection, DetailCollectionResponseModel.class);

        Pageable pageable = PageRequest.of(0, 100);
        List<Document> documents = documentRepository.findByCollectionForGuest(collection, s, pageable).getContent();
        List<DocumentResponseModel> documentResponseModels = modelMapper.map(documents, new TypeToken<List<DocumentResponseModel>>() {
        }.getType());
        int totalDocuments = documents.size();
        int totalLikes = collection.getCollectionLikes().size();

        detailCollectionResponseModel.setTotalDocuments(totalDocuments);
        detailCollectionResponseModel.setDocuments(documentResponseModels);
        detailCollectionResponseModel.setTotalLikes(totalLikes);

        return detailCollectionResponseModel;
    }
}
