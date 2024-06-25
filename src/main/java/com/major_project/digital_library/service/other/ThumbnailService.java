package com.major_project.digital_library.service.other;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ThumbnailService {
    public BufferedImage generateThumbnail(File pdfFile) {
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

            return thumbnail;
        } catch (IOException e) {
            throw new RuntimeException("Could not generate thumbnail");
        }
    }
}
