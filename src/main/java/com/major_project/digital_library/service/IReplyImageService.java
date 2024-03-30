package com.major_project.digital_library.service;

import com.major_project.digital_library.entity.ReplyImage;

public interface IReplyImageService {
    <S extends ReplyImage> S save(S entity);
}
