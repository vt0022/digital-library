package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Notification {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID notiId;

    private boolean isRead;

    private String type;

    private String message;

    private Timestamp sentAt;

    @ManyToOne
    @JoinColumn(name = "senderId")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipientId")
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "docId")
    private Document document;

    @ManyToOne
    @JoinColumn(name = "reviewId")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "replyId")
    private Reply reply;

    @ManyToOne
    @JoinColumn(name = "badgeId")
    private Badge badge;

    @OneToOne
    @JoinColumn(name = "postReportId")
    private PostReport postReport;

    @OneToOne
    @JoinColumn(name = "replyReportId")
    private ReplyReport replyReport;

    @OneToOne
    @JoinColumn(name = "postAppealId")
    private PostAppeal postAppeal;

    @OneToOne
    @JoinColumn(name = "replyAppealId")
    private ReplyAppeal replyAppeal;

    @PrePersist
    protected void onCreate() {
        sentAt = new Timestamp(System.currentTimeMillis());
    }
}
