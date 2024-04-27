package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.ReplyImage;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IReplyImageService;
import com.major_project.digital_library.util.GoogleDriveUpload;
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
@RequestMapping("/api/v2/replies")
public class ReplyImageController {
    private final IReplyImageService replyImageService;
    private final GoogleDriveUpload googleDriveUpload;

    @Autowired
    public ReplyImageController(IReplyImageService replyImageService, GoogleDriveUpload googleDriveUpload) {
        this.replyImageService = replyImageService;
        this.googleDriveUpload = googleDriveUpload;
    }

    @Operation(summary = "Tải một hình ảnh của phản hồi lên")
    @PostMapping(path = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestPart MultipartFile image) {
        FileModel gd = googleDriveUpload.uploadImage(image, image.getOriginalFilename(), null, "reply");
        ReplyImage replyImage = new ReplyImage();
        replyImage.setUrl(gd.getViewUrl());
        replyImageService.save(replyImage);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message(gd.getViewUrl())
                        .build());
    }
}
