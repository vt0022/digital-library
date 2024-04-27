package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.entity.Recency;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IDocumentService;
import com.major_project.digital_library.service.IRecencyService;
import com.major_project.digital_library.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2")
public class RecencyController {
    private final IRecencyService recencyService;
    private final IDocumentService documentService;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public RecencyController(IRecencyService recencyService, IDocumentService documentService, IUserService userService, ModelMapper modelMapper) {
        this.recencyService = recencyService;
        this.documentService = documentService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Operation(summary = "Trả về danh sách gần đây")
    @GetMapping("/documents/recent")
    public ResponseEntity<?> getRecentDocuments() {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        List<Document> documents = recencyService.findByUserOrderByAccessedAtDesc(user)
                .stream()
                .map(Recency::getDocument)
                .collect(Collectors.toList());

        List<DocumentResponseModel> documentResponseModels = modelMapper.map(documents,
                new TypeToken<List<DocumentResponseModel>>() {
                }.getType());

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get recent documents successfully")
                .data(documentResponseModels)
                .build());
    }

    @Operation(summary = "Thêm vào danh sách gần đây")
    @PostMapping("/documents/{slug}/recent")
    public ResponseEntity<?> addToRecentDocuments(@PathVariable String slug) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));
        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        Optional<Recency> recencyOptional = recencyService.findByUserAndDocument(user, document);

        if (recencyOptional.isPresent()) {
            Recency recency = recencyOptional.get();
            recency.setAccessedAt(new Timestamp(System.currentTimeMillis()));
            recencyService.save(recency);
            ;
        } else {
            List<Recency> recencies = recencyService.findByUserOrderByAccessedAtDesc(user);

            if (recencies.size() >= 10) {
                Recency recency = recencies.get(recencies.size() - 1);
                recencyService.deleteByUserAndDocument(recency.getUser(), recency.getDocument());
            }

            Recency recency = new Recency();
            recency.setDocument(document);
            recency.setUser(user);
            recency.setAccessedAt(new Timestamp(System.currentTimeMillis()));
            recencyService.save(recency);
        }

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Add to recent documents successfully")
                .build());
    }
}
