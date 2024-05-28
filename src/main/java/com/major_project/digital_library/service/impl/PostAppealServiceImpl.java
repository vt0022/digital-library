package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.NotificationMessage;
import com.major_project.digital_library.constant.ProcessStatus;
import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostAppeal;
import com.major_project.digital_library.entity.PostReport;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.PostAppealRequestModel;
import com.major_project.digital_library.model.response_model.PostAppealResponseModel;
import com.major_project.digital_library.repository.IPostAppealRepository;
import com.major_project.digital_library.repository.IPostReportRepository;
import com.major_project.digital_library.repository.IPostRepository;
import com.major_project.digital_library.service.INotificationService;
import com.major_project.digital_library.service.IPostAppealService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PostAppealServiceImpl implements IPostAppealService {
    private final IPostAppealRepository postAppealRepository;
    private final IPostRepository postRepository;
    private final IPostReportRepository postReportRepository;
    private final INotificationService notificationService;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public PostAppealServiceImpl(IPostAppealRepository postAppealRepository, IPostRepository postRepository, IPostReportRepository postReportRepository, INotificationService notificationService, IUserService userService, ModelMapper modelMapper) {
        this.postAppealRepository = postAppealRepository;
        this.postRepository = postRepository;
        this.postReportRepository = postReportRepository;
        this.notificationService = notificationService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<PostAppealResponseModel> findAllAppeals(int page, int size, String type, String status) {
        type = type.equals("all") ? null : type;
        status = status.equals("all") ? null : status;

        Pageable pageable = PageRequest.of(page, size);
        Page<PostAppeal> postAppeals = postAppealRepository.findAllPostAppeals(status, type, pageable);

        Page<PostAppealResponseModel> postAppealResponseModels = postAppeals.map(this::convertToPostAppealModel);

        return postAppealResponseModels;
    }

    @Override
    public PostAppealResponseModel appealPost(PostAppealRequestModel postAppealRequestModel) {
        PostReport postReport = postReportRepository.findById(postAppealRequestModel.getReportId()).orElseThrow(() -> new RuntimeException("Post appeal not found"));
        User user = userService.findLoggedInUser();

        PostAppeal postAppeal = new PostAppeal();
        postAppeal.setPostReport(postReport);
        postAppeal.setUser(user);
        postAppeal.setType(postAppealRequestModel.getType());
        postAppeal.setReason(postAppealRequestModel.getReason());
        postAppeal = postAppealRepository.save(postAppeal);

        PostAppealResponseModel postAppealResponseModel = modelMapper.map(postAppeal, PostAppealResponseModel.class);

        return postAppealResponseModel;
    }

    @Override
    public PostAppealResponseModel readAppeal(UUID postAppealId) {
        PostAppeal postAppeal = postAppealRepository.findById(postAppealId).orElseThrow(() -> new RuntimeException("Post appeal not found"));
        postAppeal.setStatus(ProcessStatus.REVIEWED.name());
        postAppeal = postAppealRepository.save(postAppeal);

        PostAppealResponseModel postAppealResponseModel = modelMapper.map(postAppeal, PostAppealResponseModel.class);

        return postAppealResponseModel;
    }

    @Override
    public boolean handleAppeal(UUID appealId, String type) {
        User user = userService.findLoggedInUser();
        PostAppeal postAppeal = postAppealRepository.findById(appealId).orElseThrow(() -> new RuntimeException("Post appeal not found"));
        Post post = postAppeal.getPostReport().getPost();

        if (type.equals("restore")) {
            post.setDisabled(false);
            postRepository.save(post);

            postAppeal.setStatus(ProcessStatus.RESTORED.name());
            postAppealRepository.save(postAppeal);

            notificationService.sendNotification(NotificationMessage.RESTORE_POST.name(), NotificationMessage.RESTORE_POST.getMessage(), user, postAppeal.getUser(), postAppeal);

            return true;
        } else {
            postAppeal.setStatus(ProcessStatus.REMAIN.name());
            postAppealRepository.save(postAppeal);

            return false;
        }
    }

    @Override
    public void deleteAppeal(UUID postAppealId) {
        PostAppeal postAppeal = postAppealRepository.findById(postAppealId).orElseThrow(() -> new RuntimeException("Post appeal not found"));
        postAppealRepository.delete(postAppeal);
    }

    @Override
    public PostAppealResponseModel checkAppeal(UUID postReportId) {
        PostReport postReport = postReportRepository.findById(postReportId).orElse(null);

        if (postReport == null)
            return null;
        else {
            PostAppeal postAppeal = postAppealRepository.findByPostReport(postReport).orElse(null);
            PostAppealResponseModel postAppealResponseModel = postAppeal == null ? null : convertToPostAppealModel(postAppeal);

            return postAppealResponseModel;
        }
    }

    private PostAppealResponseModel convertToPostAppealModel(PostAppeal postAppeal) {
        PostAppealResponseModel postAppealResponseModel = modelMapper.map(postAppeal, PostAppealResponseModel.class);

        return postAppealResponseModel;
    }
}
