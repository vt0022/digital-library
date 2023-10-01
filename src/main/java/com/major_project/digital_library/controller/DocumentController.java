package com.major_project.digital_library.controller;

import com.google.api.services.drive.Drive;
import com.major_project.digital_library.entity.Document;
import com.major_project.digital_library.model.response_model.FileModel;
import com.major_project.digital_library.service.CategoryService;
import com.major_project.digital_library.service.DocumentService;
import com.major_project.digital_library.service.FieldService;
import com.major_project.digital_library.service.OrganizationService;
import com.major_project.digital_library.util.GoogleDriveUpload;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.Normalizer;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/document")
public class DocumentController {
    private final Drive googleDrive;
    private final GoogleDriveUpload googleDriveUpload;
    private final DocumentService documentService;
    private final CategoryService categoryService;
    private final FieldService fieldService;
    private final OrganizationService organizationService;
    private final ModelMapper modelMapper;

    @Autowired
    public DocumentController(Drive googleDrive, GoogleDriveUpload googleDriveUpload, DocumentService documentService, CategoryService categoryService, FieldService fieldService, OrganizationService organizationService, ModelMapper modelMapper) {
        this.googleDrive = googleDrive;
        this.googleDriveUpload = googleDriveUpload;
        this.documentService = documentService;
        this.categoryService = categoryService;
        this.fieldService = fieldService;
        this.organizationService = organizationService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFileToGoogleDrive(
            @RequestParam("file") MultipartFile multipartFile) {
        FileModel gd = googleDriveUpload.uploadFile(multipartFile);
        Document doc = new Document();
        doc = modelMapper.map(gd, Document.class);
        doc.setSlug(generateSlug(doc.getDocName().replace(".pdf", "")));

        doc.setCategory(categoryService.findBySlug("giao-trinh").get());
        doc.setField(fieldService.findBySlug("nghe-thuat-am-thuc").get());

        documentService.save(doc);
        return ResponseEntity.ok(doc);
    }

    @PostMapping("/update")
    public ResponseEntity<?> uploadFileToGoogleDrive() {
        List<Document> documents = documentService.findAll();
        for(Document doc : documents) {
            //doc.setOrganization(organizationService.findById(UUID.fromString("c0a801b9-8ac0-1a60-818a-c04a8ea90019")).get());
            //doc.setVerified(true);
            Random random = new Random();
            // Generate a random integer between 10 and 200 (inclusive)
            int randomNumber = random.nextInt(181) + 20;
            doc.setTotalView(randomNumber);
            doc.setTotalFavorite(random.nextInt(15));
            documentService.save(doc);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Pageable pageable = PageRequest.of(0, 10);
        return ResponseEntity.ok(categoryService.findAll(pageable));
    }

    public String generateSlug(String name) {
        // Remove diacritics (accents) from Vietnamese characters
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        normalized = pattern.matcher(normalized).replaceAll("");

        // Random string
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        // Replace spaces with hyphens and convert to lowercase
        return normalized.trim().replaceAll(" ", "-").toLowerCase() + "-" + uuid.substring(0, 10);
    }

}
