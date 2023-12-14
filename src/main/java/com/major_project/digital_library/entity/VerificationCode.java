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
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class VerificationCode {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(length = 6)
    private int code;

    private boolean isExpired = false;

    private Timestamp createdAt;

    private Timestamp expiredAt;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        expiredAt = new Timestamp(createdAt.getTime() + 15 * 60 * 1000);
    }
}
