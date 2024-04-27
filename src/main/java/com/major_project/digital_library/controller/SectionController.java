package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.SectionResponseModel;
import com.major_project.digital_library.model.response_model.SubsectionResponseModel;
import com.major_project.digital_library.service.ISectionService;
import com.major_project.digital_library.service.ISubsectionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/sections")
public class SectionController {
    private final ISectionService sectionService;
    private final ISubsectionService subsectionService;

    @Autowired
    public SectionController(ISectionService sectionService, ISubsectionService subsectionService) {
        this.sectionService = sectionService;
        this.subsectionService = subsectionService;
    }

    @Operation(summary = "Lấy danh sách các mục đang hoạt động")
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

    @Operation(summary = "Lấy danh sách các mục có thể đăng")
    @GetMapping("/editable")
    public ResponseEntity<?> getEditableSubsections() {
        List<SubsectionResponseModel> subsectionResponseModels = subsectionService.findEditableSubsections();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get editable subsections successfully")
                .data(subsectionResponseModels)
                .build());
    }
}
