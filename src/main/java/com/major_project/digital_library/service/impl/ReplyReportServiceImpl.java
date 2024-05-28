package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.NotificationMessage;
import com.major_project.digital_library.constant.ProcessStatus;
import com.major_project.digital_library.constant.ReportReason;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyReport;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.ReplyReportRequestModel;
import com.major_project.digital_library.model.response_model.ReplyReportResponseModel;
import com.major_project.digital_library.repository.IReplyReportRepository;
import com.major_project.digital_library.repository.IReplyRepository;
import com.major_project.digital_library.service.INotificationService;
import com.major_project.digital_library.service.IReplyReportService;
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
public class ReplyReportServiceImpl implements IReplyReportService {
    private final IReplyReportRepository replyReportRepository;
    private final IReplyRepository replyRepository;
    private final IUserService userService;
    private final INotificationService notificationService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReplyReportServiceImpl(IReplyReportRepository replyReportRepository, IReplyRepository replyRepository, IUserService userService, INotificationService notificationService, ModelMapper modelMapper) {
        this.replyReportRepository = replyReportRepository;
        this.replyRepository = replyRepository;
        this.userService = userService;
        this.notificationService = notificationService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ReplyReportResponseModel> findAllReports(int page, int size, String type, String status) {
        type = type.equals("all") ? null : type;
        status = status.equals("all") ? null : status;

        Pageable pageable = PageRequest.of(page, size);
        Page<ReplyReport> replyReports = replyReportRepository.findAllReplyReports(status, type, pageable);

        Page<ReplyReportResponseModel> replyReportResponseModels = replyReports.map(this::convertToReplyReportModel);

        return replyReportResponseModels;
    }

    @Override
    public ReplyReportResponseModel reportReply(ReplyReportRequestModel replyReportRequestModel) {
        Reply reply = replyRepository.findById(replyReportRequestModel.getReplyId()).orElseThrow(() -> new RuntimeException("Reply not found"));
        User user = userService.findLoggedInUser();

        ReplyReport replyReport = new ReplyReport();
        replyReport.setReply(reply);
        replyReport.setUser(user);
        replyReport.setType(replyReportRequestModel.getType());
        replyReport.setReason(replyReportRequestModel.getReason());
        replyReport = replyReportRepository.save(replyReport);

        ReplyReportResponseModel replyReportResponseModel = modelMapper.map(replyReport, ReplyReportResponseModel.class);

        return replyReportResponseModel;
    }

    @Override
    public ReplyReportResponseModel readReport(UUID replyReportId) {
        ReplyReport replyReport = replyReportRepository.findById(replyReportId).orElseThrow(() -> new RuntimeException("Reply report not found"));
        replyReport.setStatus(ProcessStatus.REVIEWED.name());
        replyReport = replyReportRepository.save(replyReport);

        ReplyReportResponseModel replyReportResponseModel = modelMapper.map(replyReport, ReplyReportResponseModel.class);

        return replyReportResponseModel;
    }

    @Override
    public List<ReplyReportResponseModel> checkReport(UUID reportId) {
        ReplyReport replyReport = replyReportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("Reply report not found"));
        List<String> status = Arrays.asList(ProcessStatus.PENDING.name(), ProcessStatus.REVIEWED.name());
        List<ReplyReport> replyReports = replyReportRepository.findAllByReplyAndStatus(replyReport, replyReport.getReply(), status);

        List<ReplyReportResponseModel> replyReportResponseModels = replyReports.stream().map(this::convertToReplyReportModel).collect(Collectors.toList());

        return replyReportResponseModels;
    }

    @Override
    public boolean handleReport(UUID reportId, String type) {
        User user = userService.findLoggedInUser();
        ReplyReport replyReport = replyReportRepository.findById(reportId).orElseThrow(() -> new RuntimeException("Reply report not found"));
        Reply reply = replyReport.getReply();

        String reason = "";
        if (replyReport.getType().equals("KHAC"))
            reason = replyReport.getReason();
        else reason = ReportReason.valueOf(replyReport.getType()).getMessage();

        if (type.equals("disable")) {
            reply.setDisabled(true);
            replyRepository.save(reply);

            replyReport.setStatus(ProcessStatus.DISABLED.name());
            replyReportRepository.save(replyReport);

            List<String> status = Arrays.asList(ProcessStatus.PENDING.name(), ProcessStatus.REVIEWED.name());
            List<ReplyReport> relatedReplyReports = replyReportRepository.findAllByReplyAndStatus(replyReport, reply, status);
            relatedReplyReports.forEach(report -> {
                report.setStatus(ProcessStatus.DISABLED.name());
                replyReportRepository.save(report);
            });

            notificationService.sendNotification(NotificationMessage.WARN_REPLY.name(), NotificationMessage.WARN_REPLY.getMessage() + " " + reason, user, reply.getUser(), replyReport);

            return true;
        } else {
            replyRepository.delete(reply);

            replyReport.setStatus(ProcessStatus.DELETED.name());
            replyReportRepository.save(replyReport);

            notificationService.sendNotification(NotificationMessage.DELETE_REPLY.name(), NotificationMessage.DELETE_REPLY.getMessage() + " " + reason, user, reply.getUser(), reply);
            return false;
        }
    }

    @Override
    public void deleteReport(UUID replyReportId) {
        ReplyReport replyReport = replyReportRepository.findById(replyReportId).orElseThrow(() -> new RuntimeException("Reply report not found"));
        replyReportRepository.delete(replyReport);
    }

    private ReplyReportResponseModel convertToReplyReportModel(ReplyReport replyReport) {
        ReplyReportResponseModel replyReportResponseModel = modelMapper.map(replyReport, ReplyReportResponseModel.class);

        return replyReportResponseModel;
    }
}
