package com.major_project.digital_library.model.response_model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.major_project.digital_library.entity.Favorite;
import com.major_project.digital_library.entity.Review;
import com.major_project.digital_library.entity.Save;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.FieldModel;
import com.major_project.digital_library.model.OrganizationModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID docId;

    private String docName;

    private String docIntroduction;

    private String viewUrl;

    private String downloadUrl;

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

    private String author;

    private String thumbnail;

    @JsonIgnore
    private User userUploaded;

    @JsonIgnore
    private User userVerified;

    //@JsonIgnore
    private OrganizationModel organization;

    //@JsonIgnore
    private CategoryResponseModel category;

    //@JsonIgnore
    private FieldModel field;

    @JsonIgnore
    private List<Save> saves = new ArrayList<>();

    @JsonIgnore
    private List<Favorite> favorites = new ArrayList<>();

    @JsonIgnore
    private List<Review> reviews = new ArrayList<>();
}
