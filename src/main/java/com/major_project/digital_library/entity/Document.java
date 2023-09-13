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

@Entity
@Component
public class Document implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID docId;

    private String docName;

    @Column(columnDefinition = "TEXT")
    private String docIntroduction;

    private Timestamp uploadedAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

    private boolean isVerified;

    private int totalView;

    private int totalFavorite;

    private String keyword;

    private boolean isPrivate;

    private boolean isInternal;

    private String author;

    @ManyToOne
    @JoinColumn(name = "uploadedBy")
    private User userUploaded;

    @ManyToOne
    @JoinColumn(name = "verifiedBy")
    private User userVerified;

    @ManyToOne
    @JoinColumn(name = "orgId")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "fieldId")
    private Field field;

    @OneToMany(mappedBy = "document")
    private Set<Save> saves;

    @OneToMany(mappedBy = "document")
    private Set<Favorite> favorites;

    @OneToMany(mappedBy = "document")
    private Set<Review> reviews;

    @PrePersist
    protected void onCreate() {
        uploadedAt = new Timestamp(System.currentTimeMillis());
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
