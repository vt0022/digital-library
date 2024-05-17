package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.response_model.NotificationResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/notifications")
public class NotificationController {
    private final INotificationService notificationService;

    @Autowired
    public NotificationController(INotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Lấy thông báo của người dùng")
    @GetMapping("/mine")
    public ResponseEntity<?> getGeneralStatisticsForAdmin(@RequestParam(defaultValue = "10") int size
    ) {
        Page<NotificationResponseModel> notificationResponseModels = notificationService.getNotificationsOfUser(size);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get notifications of current user successfully")
                .data(notificationResponseModels)
                .build());
    }
}
