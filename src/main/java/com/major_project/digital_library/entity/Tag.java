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
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Tag {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID tagId;

    @Column(unique = true)
    private String tagName;

    @Column(unique = true)
    private String slug;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    @ManyToMany(mappedBy = "tags")
    private List<Document> documents = new ArrayList<>();

    @ManyToMany(mappedBy = "tags")
    private List<Post> posts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
}
