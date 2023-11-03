package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Document implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID docId;

    @Column(nullable = false)
    private String docName;

    @Column(length = 65535)
    private String docIntroduction;

    @Column(nullable = false)
    private String viewUrl;

    @Column(nullable = false)
    private String downloadUrl;

    @Column(nullable = false, unique = true)
    private String slug;

    private Timestamp uploadedAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

    private int verifiedStatus;

    private String note;

    private int totalView;

    private int totalFavorite;

    private boolean isPrivate;

    private boolean isInternal;

    @Column(length = 100)
    private String author;

    private String thumbnail;

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

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<Save> saves = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

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
