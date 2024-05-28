package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.other.RecaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/recaptcha")
public class RecaptchaController {

    @Autowired
    private RecaptchaService recaptchaService;

    @PostMapping("/verify")
    public ResponseEntity<?> verifyRecaptcha(@RequestParam String recaptchaResponse) {
        boolean isCaptchaValid = recaptchaService.verifyCaptcha(recaptchaResponse);

        if (!isCaptchaValid) {
            return ResponseEntity.ok(ResponseModel.builder()
                    .status(200)
                    .error(false)
                    .message("Verify recaptcha successfully")
                    .build());
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(400)
                .error(false)
                .message("Failed to verify recaptcha")
                .build());
    }
}
