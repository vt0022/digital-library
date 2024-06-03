package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Reply {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID replyId;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDisabled;

    @OneToMany(mappedBy = "parentReply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> childReplies = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parentReplyId")
    private Reply parentReply;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "repliedBy")
    private User user;

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyImage> replyImages = new ArrayList<>();

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyLike> replyLikes = new ArrayList<>();

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReplyHistory> replyHistories = new ArrayList<>();

    @OneToMany(mappedBy = "reply", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "reply", cascade = CascadeType.MERGE)
    private List<ReplyReport> replyReports = new ArrayList<>();

    @OneToOne(mappedBy = "reply", cascade = CascadeType.MERGE)
    private ReplyAcceptance replyAcceptance;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }
}
