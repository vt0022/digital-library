package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.NotificationMessage;
import com.major_project.digital_library.constant.ProcessStatus;
import com.major_project.digital_library.constant.ReportReason;
import com.major_project.digital_library.entity.Post;
import com.major_project.digital_library.entity.PostReport;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.PostReportRequestModel;
import com.major_project.digital_library.model.response_model.PostReportResponseModel;
import com.major_project.digital_library.repository.IPostReportRepository;
import com.major_project.digital_library.repository.IPostRepository;
import com.major_project.digital_library.service.INotificationService;
import com.major_project.digital_library.service.IPostReportService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostReportServiceImpl implements IPostReportService {
    private final IPostReportRepository postReportRepository;
    private final IPostRepository postRepository;
    private final INotificationService notificationService;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public PostReportServiceImpl(IPostReportRepository postReportRepository, IPostRepository postRepository, INotificationService notificationService, IUserService userService, ModelMapper modelMapper) {
        this.postReportRepository = postReportRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<PostReportResponseModel> findAllReports(int page, int size, String type, String status) {
        type = type.equals("all") ? null : type;
        status = status.equals("all") ? null : status;

        Pageable pageable = PageRequest.of(page, size);
        Page<PostReport> postReports = postReportRepository.findAllPostReports(status, type, pageable);

        Page<PostReportResponseModel> postReportResponseModels = postReports.map(this::convertToPostReportModel);

        return postReportResponseModels;
    }

    @Override
    public PostReportResponseModel reportPost(PostReportRequestModel postReportRequestModel) {
        Post post = postRepository.findById(postReportRequestModel.getPostId()).orElseThrow(() -> new RuntimeException("Post not found"));
        User user = userService.findLoggedInUser();

        PostReport postReport = new PostReport();
        postReport.setPost(post);
        postReport.setUser(user);
        postReport.setType(postReportRequestModel.getType());
        postReport.setReason(postReportRequestModel.getReason());
        postReport = postReportRepository.save(postReport);

        PostReportResponseModel postReportResponseModel = modelMapper.map(postReport, PostReportResponseModel.class);

        return postReportResponseModel;
    }

    @Override
    public PostReportResponseModel readReport(UUID postReportId) {
        PostReport postReport = postReportRepository.findById(postReportId).orElseThrow(() -> new RuntimeException("Post report not found"));
        postReport.setStatus(ProcessStatus.REVIEWED.name());
        postReport = postReportRepository.save(postReport);

        PostReportResponseModel postReportResponseModel = modelMapper.map(postReport, PostReportResponseModel.class);

        return postReportResponseModel;
    }

    public boolean handleReport(UUID reportId, String type) {
        User user = userService.findLoggedInUser();
        PostReport postReport = postReportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("Post report not found"));
        Post post = postReport.getPost();

        String reason = "";
        if (postReport.getType().equals("KHAC"))
            reason = postReport.getReason();
        else reason = ReportReason.valueOf(postReport.getType()).getMessage();

        if (type.equals("disable")) {
            post.setDisabled(true);
            postRepository.save(post);

            postReport.setStatus(ProcessStatus.DISABLED.name());
            postReportRepository.save(postReport);

            List<String> status = Arrays.asList(ProcessStatus.PENDING.name(), ProcessStatus.REVIEWED.name());
            List<PostReport> relatedPostReports = postReportRepository.findAllByPostAndStatus(postReport, post, status);
            relatedPostReports.forEach(report -> {
                report.setStatus(ProcessStatus.DISABLED.name());
                postReportRepository.save(report);
            });

            notificationService.sendNotification(NotificationMessage.WARN_POST.name(), NotificationMessage.WARN_POST.getMessage() + " " + reason, user, post.getUserPosted(), postReport);

            return true;
        } else {
            postRepository.delete(post);

            postReport.setStatus(ProcessStatus.DELETED.name());
            postReportRepository.save(postReport);

            notificationService.sendNotification(NotificationMessage.DELETE_POST.name(), NotificationMessage.DELETE_POST.getMessage(), user, post.getUserPosted(), postReport);

            return false;
        }
    }

    @Override
    public List<PostReportResponseModel> checkReport(UUID reportId) {
        PostReport postReport = postReportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("Post report not found"));
        List<String> status = Arrays.asList(ProcessStatus.PENDING.name(), ProcessStatus.REVIEWED.name());
        List<PostReport> postReports = postReportRepository.findAllByPostAndStatus(postReport, postReport.getPost(), status);

        List<PostReportResponseModel> postReportResponseModels = postReports.stream().map(this::convertToPostReportModel).collect(Collectors.toList());

        return postReportResponseModels;
    }

    @Override
    public void deleteReport(UUID postReportId) {
        PostReport postReport = postReportRepository.findById(postReportId).orElseThrow(() -> new RuntimeException("Post report not found"));
        postReportRepository.delete(postReport);
    }

    private PostReportResponseModel convertToPostReportModel(PostReport postReport) {
        PostReportResponseModel postReportResponseModel = modelMapper.map(postReport, PostReportResponseModel.class);

        return postReportResponseModel;
    }
}
