package com.major_project.digital_library.util;

import org.springframework.stereotype.Component;

@Component
public class StringHandler {
    public String getEmailUsername(String email) {
        return email.split("@")[0];

    }

    public String getFileId(String url) {
        int startIndex = url.indexOf("id=") + 3;
        int endIndex = url.length();

        return url.substring(startIndex, endIndex);
    }
}
