package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.response_model.NotificationResponseModel;
import com.major_project.digital_library.repository.INotificationRepository;
import com.major_project.digital_library.service.INotificationService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements INotificationService {
    private final INotificationRepository notificationRepository;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    public NotificationServiceImpl(INotificationRepository notificationRepository, IUserService userService, ModelMapper modelMapper) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public void sendNotification(String type, String message, User sender, User recipient, Object object) {
        Notification notification = new Notification();
        notification.setType(type);
        notification.setMessage(message);
        notification.setSender(sender);
        notification.setRecipient(recipient);
        if (object.getClass() == Document.class)
            notification.setDocument((Document) object);
        else if (object.getClass() == Post.class)
            notification.setPost((Post) object);
        else if (object.getClass() == Reply.class)
            notification.setReply((Reply) object);
        else if (object.getClass() == PostReport.class)
            notification.setPostReport((PostReport) object);
        else if (object.getClass() == ReplyReport.class)
            notification.setReplyReport((ReplyReport) object);
        else if (object.getClass() == PostAppeal.class)
            notification.setPostAppeal((PostAppeal) object);
        else if (object.getClass() == ReplyAppeal.class)
            notification.setReplyAppeal((ReplyAppeal) object);
        else if (object.getClass() == Badge.class)
            notification.setBadge((Badge) object);

        notificationRepository.save(notification);
    }

    @Override
    public Page<NotificationResponseModel> getNotificationsOfUser(int page, String status) {
        User user = userService.findLoggedInUser();

        Pageable pageable = PageRequest.of(page, 10);

        Page<Notification> notifications = Page.empty();
        if (status.equals("all"))
            notifications = notificationRepository.findAllByRecipientOrderBySentAtDesc(user, pageable);
        else
            notifications = notificationRepository.findByRecipientAndIsReadOrderBySentAtDesc(user, false, pageable);

        Page<NotificationResponseModel> notificationResponseModels = notifications.map(this::convertToNotificationModel);

        return notificationResponseModels;
    }

    @Override
    public int countUnreadNotificationsOfUser() {
        User user = userService.findLoggedInUser();

        int count = (int) notificationRepository.countAllByRecipientAndIsRead(user, false);

        return count;
    }

    @Override
    public NotificationResponseModel readNotification(UUID notiID) {
        Notification notification = notificationRepository.findById(notiID).orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notification = notificationRepository.save(notification);

        NotificationResponseModel notificationResponseModel = convertToNotificationModel(notification);

        return notificationResponseModel;
    }

    @Override
    public void deleteNotification(User sender, User recipient, String type, Object object) {
        if (object.getClass() == Post.class) {
            Optional<Notification> optionalNotification = notificationRepository.findBySenderAndRecipientAndTypeAndPost(sender, recipient, type, (Post) object);
            if (optionalNotification.isPresent()) {
                notificationRepository.delete(optionalNotification.get());
            }
        } else if (object.getClass() == Reply.class) {
            Optional<Notification> optionalNotification = notificationRepository.findBySenderAndRecipientAndTypeAndReply(sender, recipient, type, (Reply) object);
            if (optionalNotification.isPresent()) {
                notificationRepository.delete(optionalNotification.get());
            }
        }
    }

    private NotificationResponseModel convertToNotificationModel(Notification notification) {
        NotificationResponseModel notificationResponseModel = modelMapper.map(notification, NotificationResponseModel.class);

        return notificationResponseModel;
    }
}
