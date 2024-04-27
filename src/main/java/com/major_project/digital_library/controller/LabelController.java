package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.response_model.LabelResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.ILabelService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/labels")
public class LabelController {
    private final ILabelService labelService;

    public LabelController(ILabelService labelService) {
        this.labelService = labelService;
    }

    @Operation(summary = "Lấy danh sách các phần đang hoạt động")
    @GetMapping("/active")
    public ResponseEntity<?> getActiveSections() {
        List<LabelResponseModel> labelResponseModels = labelService.findActiveLabels();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get active labels successfully")
                .data(labelResponseModels)
                .build());
    }
}
