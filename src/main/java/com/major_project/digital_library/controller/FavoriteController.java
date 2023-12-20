package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Favorite;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IDocumentService;
import com.major_project.digital_library.service.IFavoriteService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class FavoriteController {
    private final IFavoriteService favoriteService;
    private final IUserService userService;
    private final IDocumentService documentService;
    private final ModelMapper modelMapper;

    @Autowired
    public FavoriteController(IFavoriteService favoriteService, IUserService userService, IDocumentService documentService, ModelMapper modelMapper) {
        this.favoriteService = favoriteService;
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

        int totalFavorite = (int) document.getFavorites()
                .stream()
                .filter(favorite -> favorite.isLiked())
                .count();
        ;

        String message = "";
        // Đã từng thích hoặc bỏ thích
        if (favoriteService.existsByUserAndDocument(user, document)) {
            Favorite favorite = favoriteService.findByUserAndDocument(user, document).orElseThrow(() -> new RuntimeException("Error while processing"));
            if (favorite.isLiked()) {
                // Bỏ thích
                document.setTotalFavorite(totalFavorite - 1);
                favorite.setLiked(false);
                message = "Unlike document successfully";
            } else {
                document.setTotalFavorite(totalFavorite + 1);
                favorite.setLiked(true);
                message = "Like document successfully";
            }
            favoriteService.save(favorite);
        } else { // Thích
            document.setTotalFavorite(totalFavorite + 1);
            Favorite favorite = new Favorite();
            favorite.setDocument(document);
            favorite.setUser(user);
            favorite.setLiked(true);
            favoriteService.save(favorite);
            message = "Like document successfully";
        }

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
        // Đã từng thích hoặc bỏ thích
        if (favoriteService.existsByUserAndDocument(user, document)) {
            Favorite favorite = favoriteService.findByUserAndDocument(user, document).orElseThrow(() -> new RuntimeException("Error while processing"));
            if (favorite.isLiked()) {
                message = "Liked";
            } else {
                document.setTotalFavorite(document.getTotalFavorite() + 1);
                favorite.setLiked(true);
                message = "Not liked";
            }
            favoriteService.save(favorite);
        } else { // Thích
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

        Page<Favorite> favorites = favoriteService.findByUserAndIsLiked(user, true, pageable);
        // Tìm theo người dùng với điều kiện tài liệu đó chưa xoá, công khai và được chia sẻ
        List<Document> documents = favorites.getContent()
                .stream()
                .map(Favorite::getDocument)
                .filter(document ->
                        (!document.isInternal() || document.getOrganization() == user.getOrganization()) &&
                                !document.isDeleted() &&
                                !document.getCategory().isDeleted() &&
                                !document.getOrganization().isDeleted() &&
                                !document.getField().isDeleted() &&
                                (document.getDocName().toLowerCase().contains(s.toLowerCase()) ||
                                        document.getDocIntroduction().toLowerCase().contains(s.toLowerCase())))
                .collect(Collectors.toList());
        Page<Document> documentPage = new PageImpl<>(documents, pageable, favorites.getTotalElements());
//        Page<Document> documents = documentService.findLikedDocuments(user, s, pageable);
        Page<DocumentResponseModel> documentModels = documentPage.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel.builder()
                .error(false)
                .status(200)
                .message("Get favorites successfully")
                .data(documentModels)
                .build());
    }

    private DocumentResponseModel convertToDocumentModel(Object o) {
        DocumentResponseModel documentResponseModel = modelMapper.map(o, DocumentResponseModel.class);
        return documentResponseModel;
    }

}
