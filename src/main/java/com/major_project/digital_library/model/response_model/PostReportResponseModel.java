package com.major_project.digital_library.model.response_model;

import com.major_project.digital_library.model.lean_model.PostLeanModel;
import com.major_project.digital_library.model.lean_model.UserLeanModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostReportResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID reportId;

    private Timestamp reportedAt;

    private String status;

    private String type;

    private String reason;

    private PostLeanModel post;

    private UserLeanModel user;
}
