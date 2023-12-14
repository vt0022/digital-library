package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Field;
import com.major_project.digital_library.model.request_model.FieldRequestModel;
import com.major_project.digital_library.model.response_model.FieldResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IFieldService;
import com.major_project.digital_library.util.SlugGenerator;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/v1/fields")
public class FieldController {
    private final IFieldService fieldService;
    private final ModelMapper modelMapper;
    private final SlugGenerator slugGenerator;

    @Autowired
    public FieldController(IFieldService fieldService, ModelMapper modelMapper, SlugGenerator slugGenerator) {
        this.fieldService = fieldService;
        this.modelMapper = modelMapper;
        this.slugGenerator = slugGenerator;
    }

    @Operation(summary = "Lấy danh sách tất cả lĩnh vực",
            description = "Trả về danh sách tất cả lĩnh vực cho admin quản lý")
    @GetMapping("/all")
    public ResponseEntity<?> getAllFields(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "50") int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        List<Field> fields = fieldService.findAll(pageable).getContent();
        List<FieldResponseModel> fieldResponseModels = fields.stream()
                .map(field -> modelMapper.map(field, FieldResponseModel.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all fields successfully")
                .data(fieldResponseModels)
                .build());
    }

    @Operation(summary = "Lấy danh sách lĩnh vực có thể xem được",
            description = "Trả về danh sách tất cả lĩnh vực chưa bị xoá")
    @GetMapping
    public ResponseEntity<?> getAvailableFields(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Field> fields = fieldService.findByIsDeleted(false, pageable).getContent();
        List<FieldResponseModel> fieldResponseModels = fields.stream()
                .map(field -> modelMapper.map(field, FieldResponseModel.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all fields successfully")
                .data(fieldResponseModels)
                .build());
    }

    @Operation(summary = "Lấy một lĩnh vực",
            description = "Trả về thông tin lĩnh vực")
    @GetMapping("/{fieldId}")
    public ResponseEntity<?> getField(@PathVariable UUID fieldId) {
        Field field = fieldService.findById(fieldId).orElseThrow(() -> new RuntimeException("Field not found"));
        FieldResponseModel fieldResponseModel = modelMapper.map(field, FieldResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get field successfully")
                .data(fieldResponseModel)
                .build());
    }

    @Operation(summary = "Tạo lĩnh vực mới",
            description = "Tạo một lĩnh vực tài liệu mới")
    @PostMapping
    public ResponseEntity<?> createField(@RequestBody FieldRequestModel fieldRequestModel) {
        Optional<Field> fieldOptional = fieldService.findByFieldName(fieldRequestModel.getFieldName());
        if (fieldOptional.isPresent())
            throw new RuntimeException("Field already exists");
        Field field = modelMapper.map(fieldRequestModel, Field.class);
        field.setSlug(slugGenerator.generateSlug(field.getFieldName(), false));
        field = fieldService.save(field);
        FieldResponseModel newFieldResponseModel = modelMapper.map(field, FieldResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create new field successfully")
                .data(newFieldResponseModel)
                .build());
    }

    @Operation(summary = "Cập nhật lĩnh vực",
            description = "Cập nhật lĩnh vực tài liệu đã có")
    @PutMapping("/{fieldId}")
    public ResponseEntity<?> updateField(@PathVariable UUID fieldId,
                                         @RequestBody FieldRequestModel fieldRequestModel) {
        Field field = fieldService.findById(fieldId).orElseThrow(() -> new RuntimeException("Field not found"));
        Optional<Field> fieldOptional = fieldService.findByFieldName(fieldRequestModel.getFieldName());
        if (fieldOptional.isPresent())
            if (fieldOptional.get().getFieldId() != field.getFieldId())
                throw new RuntimeException("Field already exists");

        field.setFieldName(fieldRequestModel.getFieldName());
        field.setSlug(slugGenerator.generateSlug(field.getFieldName(), false));
        field = fieldService.save(field);
        FieldResponseModel newFieldResponseModel = modelMapper.map(field, FieldResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update field successfully")
                .data(newFieldResponseModel)
                .build());
    }

    @Operation(summary = "Xoá lĩnh vực",
            description = "Xoá lĩnh vực: xoá cứng nếu chưa có tài liệu, ngược lại xoá mềm")
    @DeleteMapping("/{fieldId}")
    public ResponseEntity<?> deleteField(@PathVariable UUID fieldId) {
        Field field = fieldService.findById(fieldId).orElseThrow(() -> new RuntimeException("Field not found"));
        String message = "";
        if (field.getDocuments().isEmpty()) {
            message = "Delete field from system successfully";
            fieldService.deleteById(fieldId);
        } else {
            field.setDeleted(true);
            fieldService.save(field);
            message = "Unable to delete this field as there are documents linked to it. Status changed to deleted";
        }
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(message)
                .build());
    }

    @Operation(summary = "Kích hoạt lại lĩnh vực",
            description = "Kích hoạt lại lĩnh vực đã bị xoá mềm")
    @PutMapping("/{fieldId}/activation")
    public ResponseEntity<?> activateField(@PathVariable UUID fieldId) {
        Field field = fieldService.findById(fieldId).orElseThrow(() -> new RuntimeException("Field not found"));
        field.setDeleted(false);
        field = fieldService.save(field);
        FieldResponseModel fieldResponseModel = modelMapper.map(field, FieldResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Activate field successfully")
                .data(fieldResponseModel)
                .build());
    }
}
