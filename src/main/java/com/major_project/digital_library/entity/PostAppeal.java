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
public class PostAppeal {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID appealId;

    private Timestamp appealedAt;

    private String status;

    private String disableReason;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @OneToOne
    @JoinColumn(name = "reportId")
    private PostReport postReport;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToOne(mappedBy = "postAppeal", cascade = CascadeType.ALL, orphanRemoval = true)
    private Notification notification;

    @PrePersist
    protected void onCreate() {
        appealedAt = new Timestamp(System.currentTimeMillis());
        status = ProcessStatus.PENDING.name();
    }
}
