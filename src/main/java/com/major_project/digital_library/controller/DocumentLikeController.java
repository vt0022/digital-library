package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.DocumentLike;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IDocumentLikeService;
import com.major_project.digital_library.service.IDocumentService;
import com.major_project.digital_library.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2")
public class DocumentLikeController {
    private final IDocumentLikeService documentLikeService;
    private final IUserService userService;
    private final IDocumentService documentService;
    private final ModelMapper modelMapper;

    @Autowired
    public DocumentLikeController(IDocumentLikeService documentLikeService, IUserService userService, IDocumentService documentService, ModelMapper modelMapper) {
        this.documentLikeService = documentLikeService;
        this.userService = userService;
        this.documentService = documentService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Thích/bỏ thích một tài liệu",
            description = "Thêm/xoá một tài liệu vào/khỏi danh sách yêu thích")
    @PostMapping("/documents/{slug}/like")
    public ResponseEntity<?> likeDocument(@PathVariable String slug) {
        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        int totalFavorite = document.getDocumentLikes().size();

        String message = "";

        Optional<DocumentLike> documentLike = documentLikeService.findByUserAndDocument(user, document);
        if (documentLike.isPresent()) {
            // Bỏ thích
            document.setTotalFavorite(totalFavorite - 1);
            documentLikeService.delete(documentLike.get());
            message = "Unlike document successfully";
        } else {
            document.setTotalFavorite(totalFavorite + 1);
            DocumentLike newDocumentLike = new DocumentLike(user, document);
            documentLikeService.save(newDocumentLike);
            message = "Like document successfully";
        }
        documentService.save(document);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(message)
                .build());
    }

    @Operation(summary = "Kiểm tra trạng thái thích",
            description = "Kiểm tra đã thích hay chưa")
    @GetMapping("/documents/{slug}/liked")
    public ResponseEntity<?> checkLikedDocument(@PathVariable String slug) {
        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        String message = "";

        Optional<DocumentLike> documentLike = documentLikeService.findByUserAndDocument(user, document);
        if (documentLike.isPresent()) {
            message = "Liked";
        } else {
            message = "Not liked";
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(message)
                .build());
    }

    @Operation(summary = "Xem danh sách đã thích",
            description = "Trả về danh sách tài liệu đã thích")
    @GetMapping("/documents/liked")
    public ResponseEntity<?> getFavoriteDocuments(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "12") int size,
                                                  @RequestParam String s) {
        // Pageable
        Pageable pageable = PageRequest.of(page, size);

        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

//        Page<DocumentLike> documentLikes = documentLikeService.findByUserAndIsLiked(user, true, pageable);
        // Tìm theo người dùng với điều kiện tài liệu đó chưa xoá, công khai và được chia sẻ
        List<Document> documents = user.getDocumentLikes()
                .stream()
                .map(DocumentLike::getDocument)
                .filter(document ->
                        (!document.isInternal() || document.getOrganization() == user.getOrganization()) &&
                                !document.isDeleted() &&
                                !document.getCategory().isDeleted() &&
                                !document.getOrganization().isDeleted() &&
                                !document.getField().isDeleted() &&
                                (document.getDocName().toLowerCase().contains(s.toLowerCase()) ||
                                        document.getDocIntroduction().toLowerCase().contains(s.toLowerCase())))
                .collect(Collectors.toList());
        Page<Document> documentPage = new PageImpl<>(documents, pageable, user.getDocumentLikes().size());
//        Page<Document> documents = documentService.findLikedDocuments(user, s, pageable);
        Page<DocumentResponseModel> documentModels = documentPage.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel.builder()
                .error(false)
                .status(200)
                .message("Get documentLikes successfully")
                .data(documentModels)
                .build());
    }

    private DocumentResponseModel convertToDocumentModel(Object o) {
        DocumentResponseModel documentResponseModel = modelMapper.map(o, DocumentResponseModel.class);
        return documentResponseModel;
    }

}
