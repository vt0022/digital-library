package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.Notification;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.NotificationResponseModel;
import org.springframework.data.domain.Page;

public interface INotificationService {
    void sendNotification(String type, String message, User sender, User recipient, Object object);

    Page<NotificationResponseModel> getNotificationsOfUser(int size);

    NotificationResponseModel convertToNotificationModel(Notification notification);
}
