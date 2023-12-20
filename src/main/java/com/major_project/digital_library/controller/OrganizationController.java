package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Organization;
import com.major_project.digital_library.model.request_model.OrganizationRequestModel;
import com.major_project.digital_library.model.response_model.OrganizationResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IOrganizationService;
import com.major_project.digital_library.util.SlugGenerator;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {
    private final IOrganizationService organizationService;
    private final ModelMapper modelMapper;
    private final SlugGenerator slugGenerator;

    @Autowired
    public OrganizationController(IOrganizationService organizationService, ModelMapper modelMapper, SlugGenerator slugGenerator) {
        this.organizationService = organizationService;
        this.modelMapper = modelMapper;
        this.slugGenerator = slugGenerator;
    }

    @Operation(summary = "Lấy danh sách tất cả trường học",
            description = "Trả về danh sách tất cả trường học cho admin quản lý")
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrganizations(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "15") int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Organization> organizations = organizationService.findAll(pageable);
        Page<OrganizationResponseModel> organizationModels = organizations.map(this::convertToOrganizationModel);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all organizations successfully")
                .data(organizationModels)
                .build());
    }

    @Operation(summary = "Tìm trường học",
            description = "Trả về danh sách trường học tìm được")
    @GetMapping("/search")
    public ResponseEntity<?> searchOrganizations(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "15") int size,
                                                 @RequestParam String s) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Organization> organizations = organizationService.searchOrganization(false, s, pageable);
        Page<OrganizationResponseModel> organizationModels = organizations.map(this::convertToOrganizationModel);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Search organizations successfully")
                .data(organizationModels)
                .build());
    }

    @Operation(summary = "Lấy danh sách trường học có thể xem được",
            description = "Trả về danh sách tất cả trường học chưa bị xoá")
    @GetMapping
    public ResponseEntity<?> getAvailableOrganizations(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Organization> organizations = organizationService.findByIsDeleted(false, pageable).getContent();
        List<OrganizationResponseModel> organizationResponseModels = organizations.stream()
                .map(organization -> modelMapper.map(organization, OrganizationResponseModel.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all organizations successfully")
                .data(organizationResponseModels)
                .build());
    }

    @Operation(summary = "Lấy thông tin trường học",
            description = "Trả về thông tin trường học")
    @GetMapping("/{organizationId}")
    public ResponseEntity<?> getAOrganization(@PathVariable UUID organizationId) {
        Organization organization = organizationService.findById(organizationId).orElseThrow(() -> new RuntimeException("Organization not found"));
        OrganizationResponseModel organizationResponseModel = modelMapper.map(organization, OrganizationResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get organization successfully")
                .data(organizationResponseModel)
                .build());
    }

    @Operation(summary = "Tạo trường học mới",
            description = "Tạo một trường học tài liệu mới")
    @PostMapping
    public ResponseEntity<?> createOrganization(@RequestBody OrganizationRequestModel organizationRequestModel) {
        Optional<Organization> organizationOptional = organizationService.findByOrgName(organizationRequestModel.getOrgName());
        if (organizationOptional.isPresent())
            throw new RuntimeException("Organization already exists");

        Organization organization = modelMapper.map(organizationRequestModel, Organization.class);
        organization.setSlug(slugGenerator.generateSlug(organization.getOrgName(), false));
        organization = organizationService.save(organization);
        OrganizationResponseModel newOrganizationResponseModel = modelMapper.map(organization, OrganizationResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create new organization successfully")
                .data(newOrganizationResponseModel)
                .build());
    }

    @Operation(summary = "Cập nhật trường học",
            description = "Cập nhật trường học tài liệu đã có")
    @PutMapping("/{organizationId}")
    public ResponseEntity<?> updateOrganization(@PathVariable UUID organizationId,
                                                @RequestBody OrganizationRequestModel organizationRequestModel) {
        Organization organization = organizationService.findById(organizationId).orElseThrow(() -> new RuntimeException("Organization not found"));
        Optional<Organization> organizationOptional = organizationService.findByOrgName(organizationRequestModel.getOrgName());
        if (organizationOptional.isPresent())
            if (organizationOptional.get().getOrgId() != organization.getOrgId())
                throw new RuntimeException("Organization already exists");

        organization.setOrgName(organizationRequestModel.getOrgName());
        organization.setSlug(slugGenerator.generateSlug(organization.getOrgName(), false));
        organization = organizationService.save(organization);
        OrganizationResponseModel newOrganizationResponseModel = modelMapper.map(organization, OrganizationResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update organization successfully")
                .data(newOrganizationResponseModel)
                .build());
    }

    @Operation(summary = "Xoá trường học",
            description = "Xoá trường học: xoá cứng nếu chưa có tài liệu, ngược lại xoá mềm")
    @DeleteMapping("/{organizationId}")
    public ResponseEntity<?> deleteOrganization(@PathVariable UUID organizationId) {
        Organization organization = organizationService.findById(organizationId).orElseThrow(() -> new RuntimeException("Organization not found"));
        String message = "";
        if (organization.getDocuments().isEmpty()) {
            message = "Delete organization from system successfully";
            organizationService.deleteById(organizationId);
        } else {
            organization.setDeleted(true);
            organizationService.save(organization);
            message = "Unable to delete this organization as there are documents and users linked to it. Status changed to deleted";
        }
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(message)
                .build());
    }

    @Operation(summary = "Kích hoạt lại trường học",
            description = "Kích hoạt lại trường học đã bị xoá mềm")
    @PutMapping("/{organizationId}/activation")
    public ResponseEntity<?> activateOrganization(@PathVariable UUID organizationId) {
        Organization organization = organizationService.findById(organizationId).orElseThrow(() -> new RuntimeException("Organization not found"));
        organization.setDeleted(false);
        organization = organizationService.save(organization);
        OrganizationResponseModel organizationResponseModel = modelMapper.map(organization, OrganizationResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Activate organization successfully")
                .data(organizationResponseModel)
                .build());
    }

    private OrganizationResponseModel convertToOrganizationModel(Object o) {
        OrganizationResponseModel organizationResponseModel = modelMapper.map(o, OrganizationResponseModel.class);
        return organizationResponseModel;
    }
}
