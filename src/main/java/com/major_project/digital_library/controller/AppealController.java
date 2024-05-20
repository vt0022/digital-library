package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.request_model.PostAppealRequestModel;
import com.major_project.digital_library.model.request_model.ReplyAppealRequestModel;
import com.major_project.digital_library.model.response_model.PostAppealResponseModel;
import com.major_project.digital_library.model.response_model.ReplyAppealResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IPostAppealService;
import com.major_project.digital_library.service.IReplyAppealService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v2/appeals")
public class AppealController {
    private final IPostAppealService postAppealService;
    private final IReplyAppealService replyAppealService;

    @Autowired
    public AppealController(IPostAppealService postAppealService, IReplyAppealService replyAppealService) {
        this.postAppealService = postAppealService;
        this.replyAppealService = replyAppealService;
    }

    @Operation(summary = "Xem danh sách khiếu nại bài đăng")
    @GetMapping("/post")
    public ResponseEntity<?> getAllPostAppeals(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "all") String type,
                                               @RequestParam(defaultValue = "all") String status
    ) {
        Page<PostAppealResponseModel> postAppealResponseModels = postAppealService.findAllAppeals(page, size, type, status);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get post appeals successfully")
                .data(postAppealResponseModels)
                .build());
    }

    @Operation(summary = "Báo cáo bài đăng")
    @PostMapping("/post")
    public ResponseEntity<?> appealPost(@RequestBody PostAppealRequestModel postAppealRequestModel
    ) {
        PostAppealResponseModel postAppealResponseModel = postAppealService.appealPost(postAppealRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Appeal post successfully")
                .data(postAppealResponseModel)
                .build());
    }

    @Operation(summary = "Xem khiếu nại bài đăng")
    @PutMapping("/post/{appealId}/read")
    public ResponseEntity<?> readPostAppeal(@PathVariable UUID appealId
    ) {
        PostAppealResponseModel postAppealResponseModel = postAppealService.readAppeal(appealId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Read appeal successfully")
                .data(postAppealResponseModel)
                .build());
    }

    @Operation(summary = "Xoá khiếu nại bài đăng")
    @DeleteMapping("/post/{appealId}")
    public ResponseEntity<?> deletePostAppeal(@PathVariable UUID appealId
    ) {
        postAppealService.deleteAppeal(appealId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Delete appeal successfully")
                .build());
    }

    @PostMapping("/post/{appealId}/handle")
    public ResponseEntity<?> handlePostAppeal(@PathVariable UUID appealId,
                                              @RequestParam String type
    ) {
        boolean isRestored = postAppealService.handleAppeal(appealId, type);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message((isRestored ? "Restore" : "Remain disabled") + " post successfully")
                .build());
    }

    @Operation(summary = "Kiểm tra đã khiếu nại chưa")
    @GetMapping("/post/{reportId}/check")
    public ResponseEntity<?> checkPostAppeal(@PathVariable UUID reportId) {
        PostAppealResponseModel postAppealResponseModel = postAppealService.checkAppeal(reportId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Check post appeal successfully")
                .data(postAppealResponseModel)
                .build());
    }

    @Operation(summary = "Xem danh sách khiếu nại phản hồi")
    @GetMapping("/reply")
    public ResponseEntity<?> getAllReplyAppeals(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(defaultValue = "all") String type,
                                                @RequestParam(defaultValue = "all") String status
    ) {
        Page<ReplyAppealResponseModel> replyAppealResponseModels = replyAppealService.findAllAppeals(page, size, type, status);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get reply appeals successfully")
                .data(replyAppealResponseModels)
                .build());
    }

    @Operation(summary = "Báo cáo phản hồi")
    @PostMapping("/reply")
    public ResponseEntity<?> appealReply(@RequestBody ReplyAppealRequestModel replyAppealRequestModel
    ) {
        ReplyAppealResponseModel replyAppealResponseModel = replyAppealService.appealReply(replyAppealRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Appeal reply successfully")
                .data(replyAppealResponseModel)
                .build());
    }

    @Operation(summary = "Xem khiếu nại phản hồi")
    @PutMapping("/reply/{appealId}/read")
    public ResponseEntity<?> readReplyAppeal(@PathVariable UUID appealId
    ) {
        ReplyAppealResponseModel replyAppealResponseModel = replyAppealService.readAppeal(appealId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Read appeal successfully")
                .data(replyAppealResponseModel)
                .build());
    }

    @Operation(summary = "Xoá khiếu nại phản hồi")
    @DeleteMapping("/reply/{appealId}")
    public ResponseEntity<?> deleteReplyAppeal(@PathVariable UUID appealId
    ) {
        replyAppealService.deleteAppeal(appealId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Delete appeal successfully")
                .build());
    }

    @PostMapping("/reply/{appealId}/handle")
    public ResponseEntity<?> handleReplyAppeal(@PathVariable UUID appealId,
                                               @RequestParam String type
    ) {
        boolean isRestored = replyAppealService.handleAppeal(appealId, type);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message((isRestored ? "Restore" : "Remain disabled") + " reply successfully")
                .build());
    }

    @GetMapping("/reply/{reportId}/check")
    public ResponseEntity<?> checkReplyAppeal(@PathVariable UUID reportId) {
        PostAppealResponseModel postAppealResponseModel = postAppealService.checkAppeal(reportId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Check post appeal successfully")
                .data(postAppealResponseModel)
                .build());
    }
}
