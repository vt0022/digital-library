package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.SectionResponseModel;
import com.major_project.digital_library.service.ISectionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sections")
public class SectionController {
    private final ISectionService sectionService;

    @Autowired
    public SectionController(ISectionService sectionService) {
        this.sectionService = sectionService;
    }

    @Operation(summary = "Lấy danh sách các phần đang hoạt động")
    @GetMapping("/active")
    public ResponseEntity<?> getActiveSections() {
        List<SectionResponseModel> sectionResponseModels = sectionService.findActiveSections();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get active sections successfully")
                .data(sectionResponseModels)
                .build());
    }
}
