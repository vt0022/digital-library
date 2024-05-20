package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.request_model.PostReportRequestModel;
import com.major_project.digital_library.model.request_model.ReplyReportRequestModel;
import com.major_project.digital_library.model.response_model.PostReportResponseModel;
import com.major_project.digital_library.model.response_model.ReplyReportResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IPostReportService;
import com.major_project.digital_library.service.IReplyReportService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/reports")
public class ReportController {
    private final IPostReportService postReportService;
    private final IReplyReportService replyReportService;

    @Autowired
    public ReportController(IPostReportService postReportService, IReplyReportService replyReportService) {
        this.postReportService = postReportService;
        this.replyReportService = replyReportService;
    }

    @Operation(summary = "Xem danh sách báo cáo bài đăng")
    @GetMapping("/post")
    public ResponseEntity<?> getAllPostReports(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "all") String type,
                                               @RequestParam(defaultValue = "all") String status
    ) {
        Page<PostReportResponseModel> postReportResponseModels = postReportService.findAllReports(page, size, type, status);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get post reports successfully")
                .data(postReportResponseModels)
                .build());
    }

    @Operation(summary = "Báo cáo bài đăng")
    @PostMapping("/post")
    public ResponseEntity<?> reportPost(@RequestBody PostReportRequestModel postReportRequestModel
    ) {
        PostReportResponseModel postReportResponseModel = postReportService.reportPost(postReportRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Report post successfully")
                .data(postReportResponseModel)
                .build());
    }

    @Operation(summary = "Xem báo cáo bài đăng")
    @PutMapping("/post/{reportId}/read")
    public ResponseEntity<?> readPostReport(@PathVariable UUID reportId
    ) {
        PostReportResponseModel postReportResponseModel = postReportService.readReport(reportId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Read report successfully")
                .data(postReportResponseModel)
                .build());
    }

    @Operation(summary = "Xoá báo cáo bài đăng")
    @DeleteMapping("/post/{reportId}")
    public ResponseEntity<?> deletePostReport(@PathVariable UUID reportId
    ) {
        postReportService.deleteReport(reportId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Delete report successfully")
                .build());
    }

    @Operation(summary = "Kiểm tra tất cả báo cáo liên quan tới bài đăng")
    @GetMapping("/post/{reportId}/check")
    public ResponseEntity<?> checkExistingPostReports(@PathVariable UUID reportId
    ) {
        List<PostReportResponseModel> postReportResponseModels = postReportService.checkReport(reportId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Check post reports of post successfully")
                .data(postReportResponseModels)
                .build());
    }

    @Operation(summary = "Xử lý báo cáo bài đăng")
    @PostMapping("/post/{reportId}/handle")
    public ResponseEntity<?> handlePostReport(@PathVariable UUID reportId,
                                              @RequestParam String type
    ) {
        boolean isDisabled = postReportService.handleReport(reportId, type);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message((isDisabled ? "Disable" : "Delete") + " post successfully")
                .build());
    }

    @Operation(summary = "Xem danh sách báo cáo phản hồi")
    @GetMapping("/reply")
    public ResponseEntity<?> getAllReplyReports(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(defaultValue = "all") String type,
                                                @RequestParam(defaultValue = "all") String status
    ) {
        Page<ReplyReportResponseModel> replyReportResponseModels = replyReportService.findAllReports(page, size, type, status);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get reply reports successfully")
                .data(replyReportResponseModels)
                .build());
    }

    @Operation(summary = "Báo cáo phản hồi")
    @PostMapping("/reply")
    public ResponseEntity<?> reportReply(@RequestBody ReplyReportRequestModel replyReportRequestModel
    ) {
        ReplyReportResponseModel replyReportResponseModel = replyReportService.reportReply(replyReportRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Report reply successfully")
                .data(replyReportResponseModel)
                .build());
    }

    @Operation(summary = "Xem báo cáo phản hồi")
    @PutMapping("/reply/{replyId}/read")
    public ResponseEntity<?> readReplyReport(@PathVariable UUID replyId
    ) {
        ReplyReportResponseModel replyReportResponseModel = replyReportService.readReport(replyId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Read report successfully")
                .data(replyReportResponseModel)
                .build());
    }

    @Operation(summary = "Xoá báo cáo phản hồi")
    @DeleteMapping("/reply/{replyId}")
    public ResponseEntity<?> deleteReplyReport(@PathVariable UUID replyId
    ) {
        replyReportService.deleteReport(replyId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Delete report successfully")
                .build());
    }

    @Operation(summary = "Kiểm tra tất cả báo cáo liên quan tới phản hồi")
    @GetMapping("/reply/{reportId}/check")
    public ResponseEntity<?> checkExistingReplyReports(@PathVariable UUID reportId
    ) {
        List<ReplyReportResponseModel> replyReportResponseModels = replyReportService.checkReport(reportId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Check reply reports of post successfully")
                .data(replyReportResponseModels)
                .build());
    }

    @Operation(summary = "Xử lý báo cáo phản hồi")
    @PostMapping("/reply/{reportId}/handle")
    public ResponseEntity<?> handleReplyReport(@PathVariable UUID reportId,
                                               @RequestParam String type
    ) {
        boolean isDisabled = replyReportService.handleReport(reportId, type);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message((isDisabled ? "Disable" : "Delete") + " reply successfully")
                .build());
    }
}
