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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
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
    @PostMapping("/documents/{docId}/like")
    public ResponseEntity<?> likeDocument(@PathVariable UUID docId) {
        // Find user info
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(auth.getPrincipal());
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Email is not valid"));

        Document document = documentService.findById(docId).orElseThrow(() -> new RuntimeException("Document not found"));

        String message = "";
        // Đã từng thích hoặc bỏ thích
        if (favoriteService.existsByUserAndDocument(user, document)) {
            Favorite favorite = favoriteService.findByUserAndDocument(user, document).orElseThrow(() -> new RuntimeException("Error while processing"));
            if (favorite.isLiked()) {
                // Bỏ thích
                favorite.setLiked(false);
                message = "Unlike document successfully";
            } else {
                favorite.setLiked(true);
                message = "Like document successfully";
            }
            favoriteService.save(favorite);
        } else { // Thích
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

    @Operation(summary = "Xem danh sách đã thích",
            description = "Trả về danh sách tài liệu đã thích")
    @GetMapping("/documents/liked")
    public ResponseEntity<?> getFavoriteDocuments(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        // Pageable
        Pageable pageable = PageRequest.of(page, size);

        // Find user info
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(auth.getPrincipal());
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Email is not valid"));

        Page<Favorite> favorites = favoriteService.findByUserAndIsLiked(user, true, pageable);
        // Tìm theo người dùng với điều kiện tài liệu đó chưa xoá, công khai và được chia sẻ
        List<Document> documents = favorites.getContent()
                .stream()
                .filter(favorite -> (
                        !favorite.getDocument().isInternal()
                                && !favorite.getDocument().isPrivate()
                                && !favorite.getDocument().isDeleted()
                ))
                .map(Favorite::getDocument)
                .collect(Collectors.toList());
        Page<Document> documentPage = new PageImpl<>(documents, pageable, favorites.getTotalElements());
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
