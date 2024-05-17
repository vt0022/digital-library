package com.major_project.digital_library.model.response_model;

import com.major_project.digital_library.model.lean_model.PostLeanModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private PostLeanModel post;

    private Timestamp likedAt;
}
