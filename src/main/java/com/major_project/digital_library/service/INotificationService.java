package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.NotificationResponseModel;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface INotificationService {
    void sendNotification(String type, String message, User sender, User recipient, Object object);

    Page<NotificationResponseModel> getNotificationsOfUser(int page, String status);

    int countUnreadNotificationsOfUser();

    NotificationResponseModel readNotification(UUID notiID);
}
