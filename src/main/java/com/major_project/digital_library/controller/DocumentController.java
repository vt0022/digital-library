package com.major_project.digital_library.controller;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.major_project.digital_library.util.GoogleDriveUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/document")
public class DocumentController {
    private final Drive googleDrive;
    private final GoogleDriveUpload googleDriveUpload;

    @Autowired
    public DocumentController(Drive googleDrive, GoogleDriveUpload googleDriveUpload) {
        this.googleDrive = googleDrive;
        this.googleDriveUpload = googleDriveUpload;
    }

    @PostMapping("/upload")
    public File uploadFileToGoogleDrive(
            @RequestParam("file") MultipartFile multipartFile) {
        return googleDriveUpload.uploadFile(multipartFile);
    }
}
