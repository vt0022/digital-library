package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Save;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IDocumentService;
import com.major_project.digital_library.service.ISaveService;
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
public class SaveController {
    private final ISaveService saveService;
    private final IUserService userService;
    private final IDocumentService documentService;
    private final ModelMapper modelMapper;

    @Autowired
    public SaveController(ISaveService saveService, IUserService userService, IDocumentService documentService, ModelMapper modelMapper) {
        this.saveService = saveService;
        this.userService = userService;
        this.documentService = documentService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Lưu/bỏ lưu một tài liệu",
            description = "Thêm/xoá một tài liệu vào/khỏi danh sách đã lưu")
    @PostMapping("/documents/{slug}/save")
    public ResponseEntity<?> saveDocument(@PathVariable String slug) {
        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        String message = "";

        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        // Đã từng lưu hoặc bỏ lưu
        if (saveService.existsByUserAndDocument(user, document)) {
            Save save = saveService.findByUserAndDocument(user, document).orElseThrow(() -> new RuntimeException("Error while processing"));
            if (save.isSaved()) {
                // Bỏ lưu
                save.setSaved(false);
                message = "Unsave document successfully";
            } else {
                save.setSaved(true);
                message = "Save document successfully";
            }
            saveService.save(save);
        } else { // Lưu
            Save save = new Save();
            save.setDocument(document);
            save.setUser(user);
            save.setSaved(true);
            saveService.save(save);
            message = "Save document successfully";
        }
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message(message)
                .build());
    }

    @Operation(summary = "Kiểm tra trạng thái lưu",
            description = "Trả về trạng thái lưu hay chưa lưu")
    @GetMapping("/documents/{slug}/saved")
    public ResponseEntity<?> checkSaved(@PathVariable String slug) {
        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        String message = "";

        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));
        // Đã từng lưu hoặc bỏ lưu
        if (saveService.existsByUserAndDocument(user, document)) {
            Save save = saveService.findByUserAndDocument(user, document).orElseThrow(() -> new RuntimeException("Error while processing"));
            if (save.isSaved()) {
                message = "Saved";
            } else {
                save.setSaved(true);
                message = "Not saved";
            }
        } else {
            message = "Not saved";
        }
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message(message)
                .build());
    }

    @Operation(summary = "Xem danh sách đã lưu",
            description = "Trả về danh sách tài liệu đã lưu")
    @GetMapping("/documents/saved")
    public ResponseEntity<?> getFavoriteDocuments(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "12") int size,
                                                  @RequestParam String s) {
        // Pageable
        Pageable pageable = PageRequest.of(page, size);

        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Page<Save> saves = saveService.findByUserAndIsSaved(user, true, pageable);
        List<Document> documents = saves.getContent()
                .stream()
                .map(Save::getDocument)
                .filter(document ->
                        (!document.isInternal() || document.getOrganization() == user.getOrganization()) &&
                                !document.isDeleted() &&
                                !document.getCategory().isDeleted() &&
                                !document.getOrganization().isDeleted() &&
                                !document.getField().isDeleted() &&
                                (document.getDocName().toLowerCase().contains(s.toLowerCase()) ||
                                        document.getDocIntroduction().toLowerCase().contains(s.toLowerCase())))
                .collect(Collectors.toList());
        Page<Document> documentPage = new PageImpl<>(documents, pageable, saves.getTotalElements());
        Page<DocumentResponseModel> documentModels = documentPage.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel.builder()
                .error(false)
                .status(200)
                .message("Get saved documents successfully")
                .data(documentModels)
                .build());
    }

    private DocumentResponseModel convertToDocumentModel(Object o) {
        DocumentResponseModel documentResponseModel = modelMapper.map(o, DocumentResponseModel.class);
        return documentResponseModel;
    }
}
