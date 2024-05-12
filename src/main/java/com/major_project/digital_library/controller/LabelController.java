package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.request_model.LabelRequestModel;
import com.major_project.digital_library.model.response_model.LabelResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.ILabelService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/labels")
public class LabelController {
    private final ILabelService labelService;

    public LabelController(ILabelService labelService) {
        this.labelService = labelService;
    }

    @Operation(summary = "Lấy danh sách các nhãn đang hoạt động")
    @GetMapping("/active")
    public ResponseEntity<?> getActiveLabels() {
        List<LabelResponseModel> labelResponseModels = labelService.findActiveLabels();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get active labels successfully")
                .data(labelResponseModels)
                .build());
    }

    @Operation(summary = "Lấy tất cả nhãn do admin quản lý")
    @GetMapping("/all")
    public ResponseEntity<?> getAllLabels(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "100") int size,
                                          @RequestParam(defaultValue = "all") String disabled,
                                          @RequestParam(defaultValue = "") String s) {
        Page<LabelResponseModel> labelResponseModels = labelService.findAllLabels(disabled, s, page, size);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all labels successfully")
                .data(labelResponseModels)
                .build());
    }

    @Operation(summary = "Xem chi tiết 1 mục",
            description = "Trả về chi tiết 1 mục")
    @GetMapping("/{labelId}")
    public ResponseEntity<?> createLabel(@PathVariable UUID labelId) {
        LabelResponseModel labelResponseModel = labelService.findLabel(labelId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get label successfully")
                .data(labelResponseModel)
                .build());
    }

    @Operation(summary = "Tạo mục mới",
            description = "Tạo một mục mới")
    @PostMapping
    public ResponseEntity<?> createLabel(@RequestBody LabelRequestModel labelRequestModel) {
        LabelResponseModel labelResponseModel = labelService.createLabel(labelRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create new label successfully")
                .data(labelResponseModel)
                .build());
    }

    @Operation(summary = "Cập nhật mục",
            description = "Cập nhật mục đã có")
    @PutMapping("/{labelId}")
    public ResponseEntity<?> updateLabel(@PathVariable UUID labelId,
                                         @RequestBody LabelRequestModel labelRequestModel) {
        LabelResponseModel labelResponseModel = labelService.updateLabel(labelId, labelRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update label successfully")
                .data(labelResponseModel)
                .build());
    }

    @Operation(summary = "Xoá mục",
            description = "Xoá mục: xoá cứng nếu chưa có bài đăng, ngược lại xoá mềm")
    @DeleteMapping("/{labelId}")
    public ResponseEntity<?> deleteLabel(@PathVariable UUID labelId) {
        boolean isDeleted = labelService.deleteLabel(labelId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(isDeleted ? "Delete label from system successfully" : "Unable to delete this label as there are documents and users linked to it. Status changed to disabled")
                .build());
    }

    @Operation(summary = "Kích hoạt lại mục",
            description = "Kích hoạt lại mục đã bị xoá mềm")
    @PutMapping("/{labelId}/activation")
    public ResponseEntity<?> activateLabel(@PathVariable UUID labelId) {
        LabelResponseModel labelResponseModel = labelService.activateLabel(labelId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Activate label successfully")
                .data(labelResponseModel)
                .build());
    }
}
