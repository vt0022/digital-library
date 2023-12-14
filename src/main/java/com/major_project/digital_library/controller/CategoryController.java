package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.Category;
import com.major_project.digital_library.model.request_model.CategoryRequestModel;
import com.major_project.digital_library.model.response_model.CategoryResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.ICategoryService;
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
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final ICategoryService categoryService;
    private final ModelMapper modelMapper;
    private final SlugGenerator slugGenerator;

    @Autowired
    public CategoryController(ICategoryService categoryService, ModelMapper modelMapper, SlugGenerator slugGenerator) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
        this.slugGenerator = slugGenerator;
    }

    @Operation(summary = "Lấy danh sách tất cả danh mục",
            description = "Trả về danh sách tất cả danh mục cho admin quản lý")
    @GetMapping("/all")
    public ResponseEntity<?> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "50") int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        List<Category> categories = categoryService.findAll(pageable).getContent();
        List<CategoryResponseModel> categoryResponseModels = categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponseModel.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all categories successfully")
                .data(categoryResponseModels)
                .build());
    }

    @Operation(summary = "Lấy danh sách danh mục có thể xem được",
            description = "Trả về danh sách tất cả danh mục chưa bị xoá")
    @GetMapping
    public ResponseEntity<?> getAvailableCategories(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Category> categories = categoryService.findByIsDeleted(false, pageable).getContent();
        List<CategoryResponseModel> categoryResponseModels = categories.stream()
                .map(category -> modelMapper.map(category, CategoryResponseModel.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get all categories successfully")
                .data(categoryResponseModels)
                .build());
    }

    @Operation(summary = "Lấy thông tin một danh mục",
            description = "Trả về thông tin của một danh mục")
    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getACategory(@PathVariable UUID categoryId) {
        Category category = categoryService.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        CategoryResponseModel vategoryResponseModel = modelMapper.map(category, CategoryResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Get category successfully")
                .data(vategoryResponseModel)
                .build());
    }

    @Operation(summary = "Tạo danh mục mới",
            description = "Tạo một danh mục tài liệu mới")
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryRequestModel categoryRequestModel) {
        Optional<Category> categoryOptional = categoryService.findByCategoryName(categoryRequestModel.getCategoryName());
        if (categoryOptional.isPresent())
            throw new RuntimeException("Category already exists");

        Category category = modelMapper.map(categoryRequestModel, Category.class);
        category.setSlug(slugGenerator.generateSlug(category.getCategoryName(), false));
        category = categoryService.save(category);
        CategoryResponseModel newCategoryResponseModel = modelMapper.map(category, CategoryResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create new category successfully")
                .data(newCategoryResponseModel)
                .build());
    }

    @Operation(summary = "Cập nhật danh mục",
            description = "Cập nhật danh mục tài liệu đã có")
    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable UUID categoryId,
                                            @RequestBody CategoryRequestModel categoryRequestModel) {
        Category category = categoryService.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));

        Optional<Category> categoryOptional = categoryService.findByCategoryName(categoryRequestModel.getCategoryName());
        if (categoryOptional.isPresent())
            if (category.getCategoryId() != categoryOptional.get().getCategoryId())
                throw new RuntimeException("Category already exists");

        category.setCategoryName(categoryRequestModel.getCategoryName());
        category.setSlug(slugGenerator.generateSlug(category.getCategoryName(), false));
        category = categoryService.save(category);
        CategoryResponseModel newCategoryResponseModel = modelMapper.map(category, CategoryResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update category successfully")
                .data(newCategoryResponseModel)
                .build());
    }

    @Operation(summary = "Xoá danh mục",
            description = "Xoá danh mục: xoá cứng nếu chưa có tài liệu, ngược lại xoá mềm")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID categoryId) {
        Category category = categoryService.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        String message = "";
        if (category.getDocuments().isEmpty()) {
            message = "Delete category from system successfully";
            categoryService.deleteById(categoryId);
        } else {
            category.setDeleted(true);
            categoryService.save(category);
            message = "Unable to delete this category as there are documents linked to it. Status changed to deleted";
        }
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message(message)
                .build());
    }

    @Operation(summary = "Kích hoạt lại danh mục",
            description = "Kích hoạt lại danh mục đã bị xoá mềm")
    @PutMapping("/{categoryId}/activation")
    public ResponseEntity<?> activateCategory(@PathVariable UUID categoryId) {
        Category category = categoryService.findById(categoryId).orElseThrow(() -> new RuntimeException("Category not found"));
        category.setDeleted(false);
        category = categoryService.save(category);
        CategoryResponseModel categoryResponseModel = modelMapper.map(category, CategoryResponseModel.class);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Activate category successfully")
                .data(categoryResponseModel)
                .build());
    }
}
