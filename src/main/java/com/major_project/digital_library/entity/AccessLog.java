package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class AccessLog implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID logId;

    private Timestamp accessTime;

    private String accessType;

    private String ipAddress;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @PrePersist
    protected void onCreate() {
        accessTime = new Timestamp(System.currentTimeMillis());
    }
}
