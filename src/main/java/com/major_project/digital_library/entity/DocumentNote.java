package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@IdClass(UserDocumentPageKey.class)
public class DocumentNote implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "docId")
    private Document document;

    @Id
    private int page;

    @Column(columnDefinition = "JSON")
    private String content;

    private Timestamp notedAt;

    @PrePersist
    protected void onCreate() {
        notedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        notedAt = new Timestamp(System.currentTimeMillis());
    }
}
