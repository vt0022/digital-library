package com.major_project.digital_library.model.response_model;

import com.major_project.digital_library.model.lean_model.ReplyLeanModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyLikeResponseModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ReplyLeanModel reply;

    private Timestamp likedAt;
}
