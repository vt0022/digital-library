package com.major_project.digital_library.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
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
public class OrganizationModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID orgId;

    private String orgName;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

    @JsonIgnore
    private List<User> users = new ArrayList<>();

    @JsonIgnore
    private List<DocumentResponseModel> documents = new ArrayList<>();
}
