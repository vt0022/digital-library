package com.major_project.digital_library.util;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.major_project.digital_library.model.response_model.FileModel;
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

    public FileModel uploadFile(MultipartFile multipartFile) {
        try {
            List<String> parents = Collections.singletonList("1QSojNGcBQLysgzLNeGhXqqoHGxh8aZyv");

            File ggDriveFile = new File();
            String fileName = multipartFile.getOriginalFilename();
            ggDriveFile.setParents(parents).setName(fileName);

            // Create temporary file
            java.io.File tempFile = java.io.File.createTempFile("temp", ".pdf");
            multipartFile.transferTo(tempFile);

            // Create FileContent from the MultipartFile bytes
            FileContent mediaContent = new FileContent(multipartFile.getContentType(), tempFile);

            // Use Google Drive API to create the file
            File file = googleDrive.files().create(ggDriveFile, mediaContent)
                    .setFields("id, name, webViewLink, webContentLink") //, thumbnailLink
                    .execute();

            FileModel gd = new FileModel();
            gd.setDocName(file.getName().replace(".pdf", ""));
            gd.setThumbnail(generateThumbnail(tempFile, fileName.replace(".pdf", ".jpg")));
            gd.setViewUrl(file.getWebViewLink());
            gd.setDownloadUrl(file.getWebContentLink());
            tempFile.delete();
            return gd;
        } catch (IOException e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }

    public String generateThumbnail(java.io.File pdfFile, String fileName) {
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
            java.io.File tempThumbnail = java.io.File.createTempFile("thumbnail", ".png");

            // Write the BufferedImage to the temporary File
            ImageIO.write(thumbnail, "png", tempThumbnail);

            // Create FileContent
            FileContent mediaContent = new FileContent("image/png", tempThumbnail);
            // Use Google Drive API to create the file
            File uploadFile = googleDrive.files().create(ggDriveFile, mediaContent)
                    .setFields("id, name, webViewLink, webContentLink, thumbnailLink")
                    .execute();

            tempThumbnail.delete();
            return uploadFile.getWebViewLink();
        } catch (IOException e) {
            // Handle exceptions
            e.printStackTrace();
            return null;
        }
    }
}
