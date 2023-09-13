package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Component
@Entity
public class Category implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID categoryId;

    @Column(length = 50)
    private String categoryName;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

    @OneToMany(mappedBy = "category")
    private Set<Document> documents;

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
