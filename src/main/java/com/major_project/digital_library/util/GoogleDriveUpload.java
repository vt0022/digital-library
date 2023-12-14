package com.major_project.digital_library.util;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.major_project.digital_library.model.FileModel;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class GoogleDriveUpload {
    private final Drive googleDrive;

    @Autowired
    public GoogleDriveUpload(Drive googleDrive) {
        this.googleDrive = googleDrive;
    }

    public void deleteFile(String fileId) {
        try {
            googleDrive.files().delete(fileId).execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public FileModel uploadFile(MultipartFile multipartFile, String name, String fileId, String thumbnailId) {
        try {
            // Set parent folder
            List<String> parents = Collections.singletonList("1QSojNGcBQLysgzLNeGhXqqoHGxh8aZyv");

            // Create Drive file and apply parent folder and name
            File ggDriveFile = new File();
            String fileName = name;
            ggDriveFile.setParents(parents).setName(fileName);

            // Create temporary file
            java.io.File tempFile = java.io.File.createTempFile("temp", ".pdf");
            multipartFile.transferTo(tempFile);

            // Create FileContent from the MultipartFile bytes
            FileContent mediaContent = new FileContent(multipartFile.getContentType(), tempFile);

            // Delete old file
            if (fileId != null) {
                try {
                    googleDrive.files().delete(fileId).execute();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            // Use Google Drive API to create the file
            File file = googleDrive.files().create(ggDriveFile, mediaContent)
                    .setFields("id, webContentLink") //, thumbnailLink
                    .execute();
            FileModel gd = new FileModel();
            gd.setName(fileName);
            gd.setThumbnail(generateThumbnail(tempFile, name.concat(".jpg"), thumbnailId));
            gd.setViewUrl("https://drive.google.com/uc?id=" + file.getId());
            gd.setDownloadUrl(file.getWebContentLink());
            tempFile.delete();
            return gd;
        } catch (IOException e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }

    public String generateThumbnail(java.io.File pdfFile, String fileName, String thumbnailId) {
        try {
            // Load the PDF and render a page as an image
            PDDocument document = PDDocument.load(pdfFile);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 72, ImageType.RGB);

            // Generate a thumbnail from the image
            BufferedImage thumbnail = Thumbnails.of(image)
                    .size(413, 585) // Set the size of the thumbnail
                    .asBufferedImage();

            // Clean up resources
            document.close();

            // Set the folder to store thumbnail
            File ggDriveFile = new File();
            List<String> parents = Collections.singletonList("1ZG7ceYvx0x6MW2-mGUjPWWg1-T11cNmq");
            ggDriveFile.setParents(parents).setName(fileName);

            // Create temporary file
            java.io.File tempThumbnail = java.io.File.createTempFile("thumbnail", ".jpg");

            // Write the BufferedImage to the temporary File
            ImageIO.write(thumbnail, "jpg", tempThumbnail);

            // Create FileContent
            FileContent mediaContent = new FileContent("image/jpeg", tempThumbnail);

            // Delete old file
            if (thumbnailId != null) {
                try {
                    googleDrive.files().delete(thumbnailId).execute();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            // Use Google Drive API to create the file
            File uploadFile = googleDrive.files().create(ggDriveFile, mediaContent)
                    .setFields("id, name, webViewLink, webContentLink, thumbnailLink")
                    .execute();

            tempThumbnail.delete();
            return "https://drive.google.com/uc?id=" + uploadFile.getId();
        } catch (IOException e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }

    public FileModel uploadImage(MultipartFile multipartFile, String fileName, String fileId) {
        try {
            // Set parent folder
            List<String> parents = Collections.singletonList("1r7aaTi7J8N6dKHv81UDS6F9yA_u3-wjO");

            // Create Drive file and apply parent folder and name
            File ggDriveFile = new File();
            ggDriveFile.setParents(parents).setName(fileName);

            // Create temporary file
            java.io.File tempFile = java.io.File.createTempFile("temp", null);
            multipartFile.transferTo(tempFile);

            // Create FileContent from the MultipartFile bytes
            FileContent mediaContent = new FileContent(multipartFile.getContentType(), tempFile);

            // Delete old file
            if (fileId != null) {
                try {
                    googleDrive.files().delete(fileId).execute();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            // Use Google Drive API to create the file
            File file = googleDrive.files().create(ggDriveFile, mediaContent)
                    .setFields("id, webContentLink") //, thumbnailLink
                    .execute();
            FileModel gd = new FileModel();
            gd.setViewUrl("https://drive.google.com/uc?id=" + file.getId());

            tempFile.delete();
            return gd;
        } catch (Exception e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }

}
