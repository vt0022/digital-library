package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Collection;
import com.major_project.digital_library.entity.CollectionDocument;
import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.CollectionRequestModel;
import com.major_project.digital_library.model.response_model.CollectionResponseModel;
import com.major_project.digital_library.model.response_model.DetailCollectionResponseModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.repository.ICollectionDocumentRepository;
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
import java.util.stream.Collectors;

@Service
public class CollectionServiceImpl implements ICollectionService {
    private final ICollectionRepository collectionRepository;
    private final IDocumentRepository documentRepository;
    private final ICollectionDocumentRepository collectionDocumentRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public CollectionServiceImpl(ICollectionRepository collectionRepository, IDocumentRepository documentRepository, ICollectionDocumentRepository collectionDocumentRepository, IUserService userService, ModelMapper modelMapper) {
        this.collectionRepository = collectionRepository;
        this.documentRepository = documentRepository;
        this.collectionDocumentRepository = collectionDocumentRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public DetailCollectionResponseModel getDetailCollection(String slug) {
        Collection collection = collectionRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Collection not found"));

        User user = userService.findLoggedInUser();

        if (collection.isPrivate() && !collection.getUser().getUserId().equals(user.getUserId()))
            return null;

        DetailCollectionResponseModel detailCollectionResponseModel = convertToDetailCollectionModel(collection);

        return detailCollectionResponseModel;
    }

    @Override
    public DetailCollectionResponseModel getDetailCollectionForGuest(String slug) {
        Collection collection = collectionRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Collection not found"));

        if (collection.isPrivate())
            return null;

        DetailCollectionResponseModel detailCollectionResponseModel = convertToDetailCollectionModelForGuest(collection);

        return detailCollectionResponseModel;
    }

    @Override
    public Page<CollectionResponseModel> getPublicCollections(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Collection> collections = collectionRepository.findAllByIsPrivate(false, pageable);
        Page<CollectionResponseModel> collectionResponseModels = collections.map(this::convertToCollectionModel);

        return collectionResponseModels;
    }

    @Override
    public Page<CollectionResponseModel> getCollectionsForUser(int page, int size) {
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Collection> collections = collectionRepository.findForUser(user, pageable);
        Page<CollectionResponseModel> collectionResponseModels = collections.map(this::convertToCollectionModel);

        return collectionResponseModels;

    }

    @Override
    public Page<CollectionResponseModel> getCollectionsOfUser(int page, int size) {
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Collection> collections = collectionRepository.findByUser(user, pageable);
        Page<CollectionResponseModel> collectionResponseModels = collections.map(this::convertToCollectionModel);

        return collectionResponseModels;

    }

    @Override
    public Page<CollectionResponseModel> getCollectionsOfCurrentUser(int page, int size) {
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Collection> collections = collectionRepository.findByUser(user, pageable);
        Page<CollectionResponseModel> collectionResponseModels = collections.map(this::convertToCollectionModel);

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

    public DetailCollectionResponseModel convertToDetailCollectionModel(Collection collection) {
        User user = userService.findLoggedInUser();

        DetailCollectionResponseModel detailCollectionResponseModel = modelMapper.map(collection, DetailCollectionResponseModel.class);

        Pageable pageable = PageRequest.of(0, 100);
        List<Document> documents = documentRepository.findByCollectionForUser(collection, user.getOrganization(), pageable).getContent();
        List<DocumentResponseModel> documentResponseModels = modelMapper.map(documents, new TypeToken<List<DocumentResponseModel>>() {
        }.getType());
        int totalDocuments = documents.size();
        boolean isMine = user.getUserId().equals(collection.getUser().getUserId());

        detailCollectionResponseModel.setTotalDocuments(totalDocuments);
        detailCollectionResponseModel.setDocuments(documentResponseModels);
        detailCollectionResponseModel.setMine(isMine);

        return detailCollectionResponseModel;
    }

    public DetailCollectionResponseModel convertToDetailCollectionModelForGuest(Collection collection) {
        DetailCollectionResponseModel detailCollectionResponseModel = modelMapper.map(collection, DetailCollectionResponseModel.class);

        Pageable pageable = PageRequest.of(0, 100);
        List<Document> documents = documentRepository.findByCollectionForGuest(collection, pageable).getContent();
        List<DocumentResponseModel> documentResponseModels = modelMapper.map(documents, new TypeToken<List<DocumentResponseModel>>() {
        }.getType());
        int totalDocuments = documents.size();

        detailCollectionResponseModel.setTotalDocuments(totalDocuments);
        detailCollectionResponseModel.setDocuments(documentResponseModels);

        return detailCollectionResponseModel;
    }
}
