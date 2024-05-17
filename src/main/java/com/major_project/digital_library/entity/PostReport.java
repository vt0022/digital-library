package com.major_project.digital_library.entity;

import com.major_project.digital_library.constant.ProcessStatus;
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
public class PostReport {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID reportId;

    private Timestamp reportedAt;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private String type;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToOne(mappedBy = "postReport", cascade = CascadeType.MERGE)
    private Notification notification;

    @PrePersist
    protected void onCreate() {
        reportedAt = new Timestamp(System.currentTimeMillis());
        status = ProcessStatus.PENDING.name();
    }
}
