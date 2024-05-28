package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.Save;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.SaveModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.repository.IDocumentRepository;
import com.major_project.digital_library.repository.ISaveRepository;
import com.major_project.digital_library.service.ISaveService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class SaveServiceImpl implements ISaveService {
    private final ISaveRepository saveRepository;
    private final IDocumentRepository documentRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public SaveServiceImpl(ISaveRepository saveRepository, IDocumentRepository documentRepository, IUserService userService, ModelMapper modelMapper) {
        this.saveRepository = saveRepository;
        this.documentRepository = documentRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void saveDocument(String slug) {
        User user = userService.findLoggedInUser();
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        Optional<Save> saveOptional = saveRepository.findByUserAndDocument(user, document);
        if (!saveOptional.isPresent()) {
            Save save = new Save();
            save.setDocument(document);
            save.setUser(user);
            save.setSavedAt(new Timestamp(System.currentTimeMillis()));

            saveRepository.save(save);
        }
    }

    @Override
    public SaveModel unsaveDocument(String slug) {
        User user = userService.findLoggedInUser();
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        Optional<Save> saveOptional = saveRepository.findByUserAndDocument(user, document);
        if (saveOptional.isPresent()) {
            SaveModel saveModel = SaveModel.builder()
                    .slug(saveOptional.get().getDocument().getSlug())
                    .savedAt(saveOptional.get().getSavedAt())
                    .build();

            saveRepository.delete(saveOptional.get());

            return saveModel;
        }

        return null;
    }

    @Override
    public void undoUnsave(String slug, SaveModel saveModel) {
        User user = userService.findLoggedInUser();
        Document document = documentRepository.findBySlug(saveModel.getSlug()).orElseThrow(() -> new RuntimeException("Document not found"));

        Optional<Save> saveOptional = saveRepository.findByUserAndDocument(user, document);
        if (!saveOptional.isPresent() && slug.equals(saveModel.getSlug())) {
            Save save = new Save();
            save.setDocument(document);
            save.setUser(user);
            save.setSavedAt(saveModel.getSavedAt());

            saveRepository.save(save);
        }
    }

    @Override
    public Page<DocumentResponseModel> getSavedDocuments(int page, int size, String s) {
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(page, size);
        Page<Document> documents = Page.empty();
        if (s.equals(""))
            documents = saveRepository.findSavedDocuments(user, user.getOrganization(), pageable);
        else
            documents = saveRepository.searchSavedDocuments(user, user.getOrganization(), s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    private DocumentResponseModel convertToDocumentModel(Document document) {
        DocumentResponseModel documentResponseModel = modelMapper.map(document, DocumentResponseModel.class);

        int totalLikes = document.getDocumentLikes().size();
        int totalReviews = (int) document.getReviews().stream().filter(review -> review.getVerifiedStatus() == 1).count();
        int totalRating = document.getReviews()
                .stream()
                .filter(review -> review.getVerifiedStatus() == 1)
                .mapToInt(Review::getStar)
                .sum();
        double averageRating = (double) totalRating / totalReviews;

        documentResponseModel.setTotalFavorite(totalLikes);
        documentResponseModel.setAverageRating(averageRating);

        return documentResponseModel;
    }
}
