package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.response_model.NotificationResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.INotificationService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    public ResponseEntity<?> getUserNotifications(@RequestParam(defaultValue = "0") int page
    ) {
        Page<NotificationResponseModel> notificationResponseModels = notificationService.getNotificationsOfUser(page);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get notifications of current user successfully")
                .data(notificationResponseModels)
                .build());
    }

    @Operation(summary = "Lấy số thông báo chưa đọc của người dùng")
    @GetMapping("/mine/count")
    public ResponseEntity<?> countUserUnreadNotifications() {
        int count = notificationService.countUnreadNotificationsOfUser();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get number of notifications of current user successfully")
                .data(count)
                .build());
    }

    @Operation(summary = "Đánh dấu đã đọc")
    @PutMapping("/{notiId}/read")
    public ResponseEntity<?> readNotification(@PathVariable UUID notiId) {
        NotificationResponseModel notificationResponseModel = notificationService.readNotification(notiId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Read a notification successfully")
                .build());
    }
}
