package com.major_project.digital_library.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class SlugGenerator {
    public static String generateSlug(String name, boolean isRandom) {
        // Remove diacritics (accents) from Vietnamese characters
        String normalized = Normalizer.normalize(name, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        normalized = pattern.matcher(normalized).replaceAll("");
        String uuid = "";
        if (isRandom) {
            // Random string
            uuid = "-" + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
        }
        // Replace spaces with hyphens and convert to lowercase
        return normalized.trim().replaceAll(" ", "-").toLowerCase() + uuid;
    }
}
