package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.DocumentLike;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.DocumentLikeModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.repository.IDocumentLikeRepository;
import com.major_project.digital_library.repository.IDocumentRepository;
import com.major_project.digital_library.service.IDocumentLikeService;
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
public class DocumentLikeServiceImpl implements IDocumentLikeService {
    private final IDocumentLikeRepository documentLikeRepository;
    private final IDocumentRepository documentRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public DocumentLikeServiceImpl(IDocumentLikeRepository documentLikeRepository, IDocumentRepository documentRepository, IUserService userService, ModelMapper modelMapper) {
        this.documentLikeRepository = documentLikeRepository;
        this.documentRepository = documentRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void likeDocument(String slug) {
        User user = userService.findLoggedInUser();
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        Optional<DocumentLike> documentLikeOptional = documentLikeRepository.findByUserAndDocument(user, document);
        if (!documentLikeOptional.isPresent()) {
            DocumentLike documentLike = new DocumentLike();
            documentLike.setDocument(document);
            documentLike.setUser(user);
            documentLike.setLikedAt(new Timestamp(System.currentTimeMillis()));

            documentLikeRepository.save(documentLike);
        }
    }

    @Override
    public DocumentLikeModel unlikeDocument(String slug) {
        User user = userService.findLoggedInUser();
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        Optional<DocumentLike> documentLikeOptional = documentLikeRepository.findByUserAndDocument(user, document);
        if (documentLikeOptional.isPresent()) {
            DocumentLikeModel documentLikeModel = DocumentLikeModel.builder()
                    .slug(documentLikeOptional.get().getDocument().getSlug())
                    .likedAt(documentLikeOptional.get().getLikedAt())
                    .build();

            documentLikeRepository.delete(documentLikeOptional.get());

            return documentLikeModel;
        }

        return null;
    }

    @Override
    public void undoUnlike(String slug, DocumentLikeModel documentLikeModel) {
        User user = userService.findLoggedInUser();
        Document document = documentRepository.findBySlug(documentLikeModel.getSlug()).orElseThrow(() -> new RuntimeException("Document not found"));

        Optional<DocumentLike> documentLikeOptional = documentLikeRepository.findByUserAndDocument(user, document);
        if (!documentLikeOptional.isPresent() && slug.equals(documentLikeModel.getSlug())) {
            DocumentLike documentLike = new DocumentLike();
            documentLike.setDocument(document);
            documentLike.setUser(user);
            documentLike.setLikedAt(documentLikeModel.getLikedAt());

            documentLikeRepository.save(documentLike);
        }
    }

    @Override
    public Page<DocumentResponseModel> getLikedDocuments(int page, int size, String s) {
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(page, size);
        Page<Document> documents = Page.empty();
        if (s.equals(""))
            documents = documentLikeRepository.findLikedDocuments(user, user.getOrganization(), pageable);
        else
            documents = documentLikeRepository.searchLikedDocuments(user, user.getOrganization(), s, pageable);

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
