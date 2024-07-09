package com.major_project.digital_library.repository;

import com.major_project.digital_library.entity.Notification;
import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findAllByRecipientOrderBySentAtDesc(User recipient, Pageable pageable);

    Page<Notification> findByRecipientAndIsReadOrderBySentAtDesc(User recipient, Boolean isRead, Pageable pageable);

    long countAllByRecipientAndIsRead(User recipient, boolean isRead);

    List<Notification> findByType(String type);

    Optional<Notification> findBySenderAndRecipientAndTypeAndPost(User sender, User recipient, String type, Post post);

    Optional<Notification> findBySenderAndRecipientAndTypeAndReply(User sender, User recipient, String type, Reply reply);
}
