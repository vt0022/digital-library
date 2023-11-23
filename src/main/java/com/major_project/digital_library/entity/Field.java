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
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Field {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID fieldId;

    @Column(unique = true, length = 100, nullable = false)
    private String fieldName;

    @Column(unique = true, length = 100, nullable = false)
    private String slug;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

    @OneToMany(mappedBy = "field")
    private List<Document> documents = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreRemove
    protected void onRemove() {
        isDeleted = true;
    }
}
