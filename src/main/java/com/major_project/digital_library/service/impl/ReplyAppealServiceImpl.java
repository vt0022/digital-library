package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.NotificationMessage;
import com.major_project.digital_library.constant.ProcessStatus;
import com.major_project.digital_library.entity.Reply;
import com.major_project.digital_library.entity.ReplyAppeal;
import com.major_project.digital_library.entity.ReplyReport;
import com.major_project.digital_library.entity.User;
import com.major_project.digital_library.model.request_model.ReplyAppealRequestModel;
import com.major_project.digital_library.model.response_model.ReplyAppealResponseModel;
import com.major_project.digital_library.repository.IReplyAppealRepository;
import com.major_project.digital_library.repository.IReplyReportRepository;
import com.major_project.digital_library.repository.IReplyRepository;
import com.major_project.digital_library.service.INotificationService;
import com.major_project.digital_library.service.IReplyAppealService;
import com.major_project.digital_library.service.IUserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReplyAppealServiceImpl implements IReplyAppealService {
    private final IReplyAppealRepository replyAppealRepository;
    private final IReplyRepository replyRepository;
    private final IReplyReportRepository replyReportRepository;
    private final INotificationService notificationService;
    private final IUserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public ReplyAppealServiceImpl(IReplyAppealRepository replyAppealRepository, IReplyRepository replyRepository, IReplyReportRepository replyReportRepository, INotificationService notificationService, IUserService userService, ModelMapper modelMapper) {
        this.replyAppealRepository = replyAppealRepository;
        this.replyRepository = replyRepository;
        this.replyReportRepository = replyReportRepository;
        this.notificationService = notificationService;
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Page<ReplyAppealResponseModel> findAllAppeals(int page, int size, String type, String status) {
        type = type.equals("all") ? null : type;
        status = status.equals("all") ? null : status;

        Pageable pageable = PageRequest.of(page, size);
        Page<ReplyAppeal> replyAppeals = replyAppealRepository.findAllReplyAppeals(status, type, pageable);

        Page<ReplyAppealResponseModel> replyAppealResponseModels = replyAppeals.map(this::convertToReplyAppealModel);

        return replyAppealResponseModels;
    }

    @Override
    public ReplyAppealResponseModel appealReply(ReplyAppealRequestModel replyAppealRequestModel) {
        ReplyReport replyReport = replyReportRepository.findById(replyAppealRequestModel.getReportId()).orElseThrow(() -> new RuntimeException("Reply appeal not found"));
        User user = userService.findLoggedInUser();

        ReplyAppeal replyAppeal = new ReplyAppeal();
        replyAppeal.setReplyReport(replyReport);
        replyAppeal.setUser(user);
        replyAppeal.setType(replyAppealRequestModel.getType());
        replyAppeal.setReason(replyAppealRequestModel.getReason());
        replyAppeal = replyAppealRepository.save(replyAppeal);

        ReplyAppealResponseModel replyAppealResponseModel = modelMapper.map(replyAppeal, ReplyAppealResponseModel.class);

        return replyAppealResponseModel;
    }

    @Override
    public ReplyAppealResponseModel readAppeal(UUID replyAppealId) {
        ReplyAppeal replyAppeal = replyAppealRepository.findById(replyAppealId).orElseThrow(() -> new RuntimeException("Reply appeal not found"));
        replyAppeal.setStatus(ProcessStatus.REVIEWED.name());
        replyAppeal = replyAppealRepository.save(replyAppeal);

        ReplyAppealResponseModel replyAppealResponseModel = modelMapper.map(replyAppeal, ReplyAppealResponseModel.class);

        return replyAppealResponseModel;
    }

    @Override
    public boolean handleAppeal(UUID appealId, String type) {
        User user = userService.findLoggedInUser();
        ReplyAppeal replyAppeal = replyAppealRepository.findById(appealId).orElseThrow(() -> new RuntimeException("Reply appeal not found"));
        Reply reply = replyAppeal.getReplyReport().getReply();

        if (type.equals("restore")) {
            reply.setDisabled(false);
            replyRepository.save(reply);

            replyAppeal.setStatus(ProcessStatus.RESTORED.name());
            replyAppealRepository.save(replyAppeal);

            notificationService.sendNotification(NotificationMessage.RESTORE_REPLY.name(), NotificationMessage.RESTORE_REPLY.getMessage(), user, replyAppeal.getUser(), replyAppeal);

            return true;
        } else {
            replyAppeal.setStatus(ProcessStatus.REMAIN.name());
            replyAppealRepository.save(replyAppeal);

            return false;
        }
    }

    @Override
    public void deleteAppeal(UUID replyAppealId) {
        ReplyAppeal replyAppeal = replyAppealRepository.findById(replyAppealId).orElseThrow(() -> new RuntimeException("Reply appeal not found"));
        replyAppealRepository.delete(replyAppeal);
    }

    private ReplyAppealResponseModel convertToReplyAppealModel(ReplyAppeal replyAppeal) {
        ReplyAppealResponseModel replyAppealResponseModel = modelMapper.map(replyAppeal, ReplyAppealResponseModel.class);

        return replyAppealResponseModel;
    }
}
