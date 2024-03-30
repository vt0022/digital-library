package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.PostImage;

public interface IPostImageService {
    <S extends PostImage> S save(S entity);
}
