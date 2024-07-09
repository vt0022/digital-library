package com.major_project.digital_library.entity;

import com.major_project.digital_library.constant.ProcessStatus;
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
public class ReplyReport {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID reportId;

    private Timestamp reportedAt;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private String type;

    @ManyToOne
    @JoinColumn(name = "reply")
    private Reply reply;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToOne(mappedBy = "replyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private ReplyAppeal replyAppeal;

    @OneToOne(mappedBy = "replyReport", cascade = CascadeType.ALL, orphanRemoval = true)
    private Notification notification;

    @PrePersist
    protected void onCreate() {
        reportedAt = new Timestamp(System.currentTimeMillis());
        status = ProcessStatus.PENDING.name();
    }
}
