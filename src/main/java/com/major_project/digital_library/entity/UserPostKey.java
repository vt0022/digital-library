package com.major_project.digital_library.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPostKey implements Serializable {
    private static final long serialVersionUID = 1L;

    private User user;

    private Post post;
}
