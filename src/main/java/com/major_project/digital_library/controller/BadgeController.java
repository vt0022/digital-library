package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.response_model.BadgeResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IBadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/badges")
public class BadgeController {
    private final IBadgeService badgeService;

    @Autowired
    public BadgeController(IBadgeService badgeService) {
        this.badgeService = badgeService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findBadgesOfUser(@PathVariable UUID userId) {
        List<BadgeResponseModel> badgeResponseModels = badgeService.findBadgesOfUser(userId);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get badges of user successfully")
                .data(badgeResponseModels)
                .build());
    }
}
