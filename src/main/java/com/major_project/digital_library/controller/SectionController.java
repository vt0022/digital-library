package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.request_model.SectionRequestModel;
import com.major_project.digital_library.model.request_model.SubsectionRequestModel;
import com.major_project.digital_library.model.response_model.DetailSectionResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.model.response_model.SectionResponseModel;
import com.major_project.digital_library.model.response_model.SubsectionResponseModel;
import com.major_project.digital_library.service.ISectionService;
import com.major_project.digital_library.service.ISubsectionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
        List<DetailSectionResponseModel> detailSectionResponseModels = sectionService.findActiveSections();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get active sections successfully")
                .data(detailSectionResponseModels)
                .build());
    }

    @Operation(summary = "Lấy tất cả mục do admin quản lý")
    @GetMapping("/all")
    public ResponseEntity<?> getAllSections(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "100") int size,
                                            @RequestParam(defaultValue = "all") String disabled,
                                            @RequestParam(defaultValue = "") String s) {
        Page<SectionResponseModel> sectionResponseModels = sectionService.findAllSections(disabled, s, page, size);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all sections successfully")
                .data(sectionResponseModels)
                .build());
    }

    @Operation(summary = "Xem chi tiết 1 mục",
            description = "Trả về chi tiết 1 mục")
    @GetMapping("/{sectionId}")
    public ResponseEntity<?> createSection(@PathVariable UUID sectionId) {
        SectionResponseModel sectionResponseModel = sectionService.findSection(sectionId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get section successfully")
                .data(sectionResponseModel)
                .build());
    }

    @Operation(summary = "Tạo mục mới",
            description = "Tạo một mục mới")
    @PostMapping
    public ResponseEntity<?> createSection(@RequestBody SectionRequestModel sectionRequestModel) {
        SectionResponseModel sectionResponseModel = sectionService.createSection(sectionRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create new section successfully")
                .data(sectionResponseModel)
                .build());
    }

    @Operation(summary = "Cập nhật mục",
            description = "Cập nhật mục đã có")
    @PutMapping("/{sectionId}")
    public ResponseEntity<?> updateSection(@PathVariable UUID sectionId,
                                           @RequestBody SectionRequestModel sectionRequestModel) {
        SectionResponseModel sectionResponseModel = sectionService.updateSection(sectionId, sectionRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update section successfully")
                .data(sectionResponseModel)
                .build());
    }

    @Operation(summary = "Xoá mục",
            description = "Xoá mục: xoá cứng nếu chưa có bài đăng, ngược lại xoá mềm")
    @DeleteMapping("/{sectionId}")
    public ResponseEntity<?> deleteSection(@PathVariable UUID sectionId) {
        boolean isDeleted = sectionService.deleteSection(sectionId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(isDeleted ? "Delete section from system successfully" : "Unable to delete this section as there are documents and users linked to it. Status changed to disabled")
                .build());
    }

    @Operation(summary = "Kích hoạt lại mục",
            description = "Kích hoạt lại mục đã bị xoá mềm")
    @PutMapping("/{sectionId}/activation")
    public ResponseEntity<?> activateSection(@PathVariable UUID sectionId) {
        SectionResponseModel sectionResponseModel = sectionService.activateSection(sectionId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Activate section successfully")
                .data(sectionResponseModel)
                .build());
    }

    @Operation(summary = "Lấy danh sách các chuyên mục có thể đăng")
    @GetMapping("/sub/editable")
    public ResponseEntity<?> getEditableSubsections() {
        List<SubsectionResponseModel> subsectionResponseModels = subsectionService.findEditableSubsections();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get editable subsections successfully")
                .data(subsectionResponseModels)
                .build());
    }

    @Operation(summary = "Lấy tất cả chuyên mục do admin quản lý")
    @GetMapping("/sub/all")
    public ResponseEntity<?> getAllSubsections(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               @RequestParam(defaultValue = "all") String editable,
                                               @RequestParam(defaultValue = "all") String disabled,
                                               @RequestParam(defaultValue = "") String s) {
        Page<SubsectionResponseModel> subsectionResponseModels = subsectionService.findAllSubsections(disabled, editable, s, page, size);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all subsections successfully")
                .data(subsectionResponseModels)
                .build());
    }

    @Operation(summary = "Xem chi tiết 1 chuyên mục",
            description = "Trả về chi tiết 1 chuyên mục")
    @GetMapping("/sub/{subsectionId}")
    public ResponseEntity<?> createSubsection(@PathVariable UUID subsectionId) {
        SubsectionResponseModel subsectionResponseModel = subsectionService.findSubsection(subsectionId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get subsection successfully")
                .data(subsectionResponseModel)
                .build());
    }

    @Operation(summary = "Tạo chuyên mục mới",
            description = "Tạo một chuyên mục mới")
    @PostMapping("/sub")
    public ResponseEntity<?> createSubsection(@RequestBody SubsectionRequestModel subsectionRequestModel) {
        SubsectionResponseModel subsectionResponseModel = subsectionService.createSubsection(subsectionRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create new subsection successfully")
                .data(subsectionResponseModel)
                .build());
    }

    @Operation(summary = "Cập nhật chuyên mục",
            description = "Cập nhật chuyên mục đã có")
    @PutMapping("/sub/{subsectionId}")
    public ResponseEntity<?> updateSubsection(@PathVariable UUID subsectionId,
                                              @RequestBody SubsectionRequestModel subsectionRequestModel) {
        SubsectionResponseModel subsectionResponseModel = subsectionService.updateSubsection(subsectionId, subsectionRequestModel);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update subsection successfully")
                .data(subsectionResponseModel)
                .build());
    }

    @Operation(summary = "Xoá chuyên mục",
            description = "Xoá chuyên mục: xoá cứng nếu chưa có bài đăng, ngược lại xoá mềm")
    @DeleteMapping("/sub/{subsectionId}")
    public ResponseEntity<?> deleteSubsection(@PathVariable UUID subsectionId) {
        boolean isDeleted = subsectionService.deleteSubsection(subsectionId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(isDeleted ? "Delete subsection from system successfully" : "Unable to delete this subsection as there are documents and users linked to it. Status changed to disabled")
                .build());
    }

    @Operation(summary = "Kích hoạt lại chuyên mục",
            description = "Kích hoạt lại chuyên mục đã bị xoá mềm")
    @PutMapping("/sub/{subsectionId}/activation")
    public ResponseEntity<?> activateSubsection(@PathVariable UUID subsectionId) {
        SubsectionResponseModel subsectionResponseModel = subsectionService.activateSubsection(subsectionId);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Activate subsection successfully")
                .data(subsectionResponseModel)
                .build());
    }
}
