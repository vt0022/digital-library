package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class PostHistory {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID postHistoryId;

    private String title;

    @Column(columnDefinition = "text")
    private String content;

    private Timestamp loggedAt;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @PrePersist
    protected void onCreate() {
        loggedAt = new Timestamp(System.currentTimeMillis());
    }
}
