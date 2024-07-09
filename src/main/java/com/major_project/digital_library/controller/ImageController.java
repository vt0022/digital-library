package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.other.GoogleDriveService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v2/images")
public class ImageController {
    private final GoogleDriveService googleDriveService;

    @Autowired
    public ImageController(GoogleDriveService googleDriveService) {
        this.googleDriveService = googleDriveService;
    }

    @Operation(summary = "Tải một hình ảnh của phản hồi lên")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestPart MultipartFile image) {
        FileModel gd = googleDriveService.uploadImage(image, image.getOriginalFilename(), null, "reply");

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(gd == null ? 400 : 200)
                        .error(false)
                        .message(gd == null ? "Error uploaded image" : "Image uploaded successfully")
                        .data(gd == null ? null : gd.getViewUrl())
                        .build());
    }
}
