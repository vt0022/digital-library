package com.major_project.digital_library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
public class PostAcceptance {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID acceptId;

    private Timestamp acceptedAt;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;

    @PrePersist
    protected void onCreate() {
        acceptedAt = new Timestamp(System.currentTimeMillis());
    }
}
