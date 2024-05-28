package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Recency;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.repository.IDocumentRepository;
import com.major_project.digital_library.repository.IRecencyRepository;
import com.major_project.digital_library.service.IRecencyService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecencyServiceImpl implements IRecencyService {
    private final IRecencyRepository recencyRepository;
    private final IDocumentRepository documentRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public RecencyServiceImpl(IRecencyRepository recencyRepository, IDocumentRepository documentRepository, IUserService userService, ModelMapper modelMapper) {
        this.recencyRepository = recencyRepository;
        this.documentRepository = documentRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<DocumentResponseModel> getRecentDocuments() {
        User user = userService.findLoggedInUser();

        List<Document> documents = recencyRepository.findRecentDocuments(user, user.getOrganization());

        List<DocumentResponseModel> documentResponseModels = documents.stream()
                .map(this::convertToDocumentModel)
                .collect(Collectors.toList());

        return documentResponseModels;
    }

    @Override
    public void addToRecentDocuments(String slug) {
        User user = userService.findLoggedInUser();
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        Optional<Recency> recencyOptional = recencyRepository.findByUserAndDocument(user, document);

        if (recencyOptional.isPresent()) {
            Recency recency = recencyOptional.get();
            recency.setAccessedAt(new Timestamp(System.currentTimeMillis()));
            recencyRepository.save(recency);
            ;
        } else {
            List<Document> documents = recencyRepository.findRecentDocuments(user, user.getOrganization());

            if (documents.size() >= 20) {
                Recency recency = recencyRepository.findByUserAndDocument(user, documents.get(documents.size() - 1)).orElse(null);
                recencyRepository.delete(recency);
            }

            Recency recency = new Recency();
            recency.setDocument(document);
            recency.setUser(user);
            recencyRepository.save(recency);
        }
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
