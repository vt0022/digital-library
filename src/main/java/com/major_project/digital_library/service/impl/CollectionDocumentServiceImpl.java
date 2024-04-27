package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Collection;
import com.major_project.digital_library.entity.CollectionDocument;
import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.model.response_model.CollectionResponseModel;
import com.major_project.digital_library.repository.ICollectionDocumentRepository;
import com.major_project.digital_library.repository.ICollectionRepository;
import com.major_project.digital_library.repository.IDocumentRepository;
import com.major_project.digital_library.service.ICollectionDocumentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollectionDocumentServiceImpl implements ICollectionDocumentService {
    private final ICollectionRepository collectionRepository;
    private final IDocumentRepository documentRepository;
    private final ICollectionDocumentRepository collectionDocumentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CollectionDocumentServiceImpl(ICollectionRepository collectionRepository, IDocumentRepository documentRepository, ICollectionDocumentRepository collectionDocumentRepository, ModelMapper modelMapper) {
        this.collectionRepository = collectionRepository;
        this.documentRepository = documentRepository;
        this.collectionDocumentRepository = collectionDocumentRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public CollectionResponseModel addToCollection(UUID collectionId, UUID docId) {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new RuntimeException("Collection not found"));
        Document document = documentRepository.findById(docId).orElseThrow(() -> new RuntimeException("Document not found"));

        if (!collectionDocumentRepository.existsByCollectionAndDocument(collection, document)) {
            CollectionDocument collectionDocument = new CollectionDocument();
            collectionDocument.setCollection(collection);
            collectionDocument.setDocument(document);
            collectionDocumentRepository.save(collectionDocument);
        }

        CollectionResponseModel collectionResponseModel = modelMapper.map(collection, CollectionResponseModel.class);

        return collectionResponseModel;
    }

    @Override
    public CollectionResponseModel removeFromCollection(UUID collectionId, UUID docId) {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> new RuntimeException("Collection not found"));
        Document document = documentRepository.findById(docId).orElseThrow(() -> new RuntimeException("Document not found"));

        if (collectionDocumentRepository.existsByCollectionAndDocument(collection, document)) {
            CollectionDocument collectionDocument = collectionDocumentRepository.findByCollectionAndDocument(collection, document).orElseThrow(() -> new RuntimeException("Document not found in collection"));
            collectionDocumentRepository.delete(collectionDocument);
        }

        CollectionResponseModel collectionResponseModel = modelMapper.map(collection, CollectionResponseModel.class);

        return collectionResponseModel;
    }

}
