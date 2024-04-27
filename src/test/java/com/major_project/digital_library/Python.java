package com.major_project.digital_library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Python {
    public static void main(String[] args) throws IOException, InterruptedException {
        String text = "Túi xách là một trong những vật dụng cần thiết của hầu hết chị em phụ nữ mỗi khi ra đường.";
        ProcessBuilder builder = new ProcessBuilder("python", "src/main/resources/y.py", " " + text);
        builder.redirectErrorStream(true);

        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
