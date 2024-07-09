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
public class ReplyHistory {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID replyHistoryId;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private Timestamp loggedAt;

    @ManyToOne
    @JoinColumn(name = "replyId")
    private Reply reply;

    @PrePersist
    protected void onCreate() {
        loggedAt = new Timestamp(System.currentTimeMillis());
    }
}
