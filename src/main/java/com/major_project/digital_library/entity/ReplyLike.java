package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@IdClass(UserReplyKey.class)
public class ReplyLike {
    @Id
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "replyId")
    private Reply reply;

    private Timestamp likedAt;

    @PrePersist
    protected void onCreate() {
        likedAt = new Timestamp(System.currentTimeMillis());
    }
}
