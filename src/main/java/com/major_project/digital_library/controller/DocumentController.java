package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.request_model.DocumentRequestModel;
import com.major_project.digital_library.model.response_model.DetailDocumentResponseModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.*;
import com.major_project.digital_library.util.GoogleDriveUpload;
import com.major_project.digital_library.util.SlugGenerator;
import com.major_project.digital_library.util.StringHandler;
import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final GoogleDriveUpload googleDriveUpload;
    private final StringHandler stringHandler;
    private final IDocumentService documentService;
    private final ICategoryService categoryService;
    private final IFieldService fieldService;
    private final IOrganizationService organizationService;
    private final IUserService userService;
    private final ModelMapper modelMapper;
    private final SlugGenerator slugGenerator;

    @Autowired
    public DocumentController(GoogleDriveUpload googleDriveUpload, StringHandler stringHandler, IDocumentService documentService, ICategoryService categoryService, IFieldService fieldService, IOrganizationService organizationService, IUserService userService, ModelMapper modelMapper, SlugGenerator slugGenerator) {
        this.googleDriveUpload = googleDriveUpload;
        this.stringHandler = stringHandler;
        this.documentService = documentService;
        this.categoryService = categoryService;
        this.fieldService = fieldService;
        this.organizationService = organizationService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.slugGenerator = slugGenerator;
    }

    @Operation(summary = "Xem chi tiết một tài liệu cho sinh viên, quản lý, admin")
    @GetMapping("/{slug}")
    public ResponseEntity<?> viewDocument(@PathVariable String slug) {
        User user = userService.findLoggedInUser().orElse(null);
        Document document = documentService.findBySlug(slug).orElse(null);

        if (document == null)
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(true)
                    .message("Document not accessible")
                    .build());

        if (user.getRole().getRoleName().equals("ROLE_STUDENT")) {
            if (user == document.getUserUploaded()) {
                if (document.isDeleted() || document.getCategory().isDeleted() ||
                        document.getField().isDeleted() || document.getOrganization().isDeleted()) {
                    return ResponseEntity.ok(ResponseModel
                            .builder()
                            .status(404)
                            .error(true)
                            .message("Document not accessible")
                            .build());
                }
            } else {
                if ((document.isInternal() && document.getOrganization() != user.getOrganization()) ||
                        document.isDeleted() || document.getVerifiedStatus() == 0 ||
                        document.getVerifiedStatus() == -1 || document.getCategory().isDeleted() ||
                        document.getField().isDeleted() || document.getOrganization().isDeleted()) {
                    return ResponseEntity.ok(ResponseModel
                            .builder()
                            .status(404)
                            .error(true)
                            .message("Document not accessible")
                            .build());
                }
            }
        } else if (user.getRole().getRoleName().equals("ROLE_MANAGER")) {
            if (user.getOrganization() != document.getUserUploaded().getOrganization()) {

                return ResponseEntity.ok(ResponseModel
                        .builder()
                        .status(404)
                        .error(true)
                        .message("Document not accessible")
                        .build());

            }
        }

        // Increase views
        document.setTotalView(document.getTotalView() + 1);
        documentService.save(document);
        DetailDocumentResponseModel documentResponseModel = modelMapper.map(document, DetailDocumentResponseModel.class);
        int totalReviews = (int) document.getReviews().stream().count();
        documentResponseModel.setTotalReviews(totalReviews);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get document successfully")
                .data(documentResponseModel)
                .build());
    }

    @Operation(summary = "Xem chi tiết một tài liệu cho khách")
    @GetMapping("/{slug}/public")
    public ResponseEntity<?> viewDocumentForGuest(@PathVariable String slug) {
        Document document = documentService.findBySlug(slug).orElse(null);

        if (document == null)
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(true)
                    .message("Document not accessible")
                    .build());


        if (document.isDeleted() || document.isInternal() || document.getVerifiedStatus() == 0 ||
                document.getVerifiedStatus() == -1 || document.getCategory().isDeleted() ||
                document.getField().isDeleted() || document.getOrganization().isDeleted()) {
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(true)
                    .message("Document not accessible")
                    .build());
        }

        // Increase views
        document.setTotalView(document.getTotalView() + 1);
        documentService.save(document);
        DetailDocumentResponseModel documentResponseModel = modelMapper.map(document, DetailDocumentResponseModel.class);
        int totalReviews = (int) document.getReviews().stream().count();
        documentResponseModel.setTotalReviews(totalReviews);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get document successfully")
                .data(documentResponseModel)
                .build());
    }

    @Operation(summary = "Xem danh sách toàn bộ tài liệu",
            description = "Trả về danh sách tất cả tài liệu trên hệ thống")
    @GetMapping()
    public ResponseEntity<?> getAllDocumentsWithFilter(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size,
                                                       @RequestParam(defaultValue = "updatedAt") String order,
                                                       @RequestParam(defaultValue = "all") String category,
                                                       @RequestParam(defaultValue = "all") String field,
                                                       @RequestParam(defaultValue = "all") String organization,
                                                       @RequestParam(defaultValue = "all") String deleted,
                                                       @RequestParam(defaultValue = "all") String internal,
                                                       @RequestParam(defaultValue = "all") String status) {
        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Organization foundOrganization = organization.equals("all") ?
                null : organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Boolean isDeleted = deleted.equals("all") ?
                null : Boolean.valueOf(deleted);

        Boolean isInternal = internal.equals("all") ?
                null : Boolean.valueOf(internal);

        Integer verifiedStatus = status.equals("all") ?
                null : Integer.valueOf(status);

        Page<Document> documents = documentService.findAllDocumentsWithFilter(isDeleted, isInternal, verifiedStatus, foundCategory, foundField, foundOrganization, pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get all documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Tìm kiếm toàn bộ tài liệu",
            description = "Trả về danh sách tất cả tài liệu tìm được trên hệ thống")
    @GetMapping("/search")
    public ResponseEntity<?> searchAllDocuments(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size,
                                                @RequestParam(defaultValue = "updatedAt") String order,
                                                @RequestParam(defaultValue = "all") String category,
                                                @RequestParam(defaultValue = "all") String field,
                                                @RequestParam(defaultValue = "all") String organization,
                                                @RequestParam(defaultValue = "all") String deleted,
                                                @RequestParam(defaultValue = "all") String internal,
                                                @RequestParam(defaultValue = "all") String status,
                                                @RequestParam String s) {

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Organization foundOrganization = organization.equals("all") ?
                null : organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Boolean isDeleted = deleted.equals("all") ?
                null : Boolean.valueOf(deleted);

        Boolean isInternal = internal.equals("all") ?
                null : Boolean.valueOf(internal);

        Integer verifiedStatus = status.equals("all") ?
                null : Integer.valueOf(status);

        Page<Document> documents = documentService.searchWithAllDocuments(isDeleted, isInternal, verifiedStatus, foundCategory, foundField, foundOrganization, s, pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Search documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Tìm kiếm tài liệu của một trường",
            description = "Trả về danh sách tất cả tài liệu của một trường tìm được trên hệ thống")
    @GetMapping("/organizations/{organization}/search")
    public ResponseEntity<?> searchAllDocumentsByOrganization(@PathVariable String organization,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size,
                                                              @RequestParam(defaultValue = "updatedAt") String order,
                                                              @RequestParam(defaultValue = "all") String category,
                                                              @RequestParam(defaultValue = "all") String field,
                                                              @RequestParam(defaultValue = "all") String deleted,
                                                              @RequestParam(defaultValue = "all") String internal,
                                                              @RequestParam(defaultValue = "all") String status,
                                                              @RequestParam String s) {

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Organization foundOrganization = organization.equals("all") ?
                null : organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Boolean isDeleted = deleted.equals("all") ?
                null : Boolean.valueOf(deleted);

        Boolean isInternal = internal.equals("all") ?
                null : Boolean.valueOf(internal);

        Integer verifiedStatus = status.equals("all") ?
                null : Integer.valueOf(status);

        Page<Document> documents = documentService.searchWithAllDocuments(isDeleted, isInternal, verifiedStatus, foundCategory, foundField, foundOrganization, s, pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Search documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu bản thân đã tải lên (sinh viên)",
            description = "Trả về danh sách tất cả tài liệu mà bản thân sinh viên đã tải lên (có phân loại)")
    @GetMapping("/myuploads")
    public ResponseEntity<?> getMyUploads(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size,
                                          @RequestParam(defaultValue = "updatedAt") String order,
                                          @RequestParam(defaultValue = "all") String category,
                                          @RequestParam(defaultValue = "all") String organization,
                                          @RequestParam(defaultValue = "all") String field,
                                          @RequestParam(defaultValue = "all") String status,
                                          @RequestParam String s) {

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Organization foundOrganization = organization.equals("all") ?
                null : organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Integer verifiedStatus = status.equals("all") ?
                null : Integer.valueOf(status);

        Page<Document> documents = documentService.findUploadedDocuments(verifiedStatus, foundCategory, foundField, foundOrganization, user, s, pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get my uploads successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu bản thân đã tải lên",
            description = "Trả về danh sách tất cả tài liệu mà bản thân đã tải lên")
    @GetMapping("/mine")
    public ResponseEntity<?> getOwnedDocuments(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size) {
        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Document> documents;
        if (user.getRole().getRoleName().equals("ROLE_ADMIN") || user.getRole().getRoleName().equals("ROLE_MANAGER"))
            documents = documentService.findByUserUploaded(user, pageable);
        else
            documents = documentService.findByUserUploadedAndIsDeleted(user, false, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get uploaded documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu một người đã tải lên",
            description = "Trả về danh sách tất cả tài liệu mà một người đã tải lên")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getDocumentsByUser(@PathVariable UUID userId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {
        // Find user info
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Document> documents = documentService.findByUserUploaded(user, pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get uploaded documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Tài liệu một người đã tải lên cho sinh viên xem",
            description = "Trả về danh sách tất cả tài liệu một người đã tải lên cho sinh viên xem)")
    @GetMapping("/view/user/{userId}")
    public ResponseEntity<?> findDocumentsByUserForStudent(@PathVariable UUID userId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size,
                                                           @RequestParam(defaultValue = "updatedAt") String order,
                                                           @RequestParam(defaultValue = "all") String category,
                                                           @RequestParam(defaultValue = "all") String field,
                                                           @RequestParam String s) {


        User user = userService.findLoggedInUser().orElse(null);

        User userUploaded = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Page<Document> documents = documentService.findUploadedDocumentsByUserForStudent(foundCategory, foundField, userUploaded.getOrganization(), userUploaded, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents by user successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Tài liệu một người đã tải lên cho khách xem)",
            description = "Trả về danh sách tất cả tài liệu một người đã tải lên cho khách xem)")
    @GetMapping("/view/user/{userId}/public")
    public ResponseEntity<?> findDocumentsByUserForGuest(@PathVariable UUID userId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "20") int size,
                                                         @RequestParam(defaultValue = "updatedAt") String order,
                                                         @RequestParam(defaultValue = "all") String category,
                                                         @RequestParam(defaultValue = "all") String field,
                                                         @RequestParam String s) {

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        User userUploaded = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Page<Document> documents = documentService.findUploadedDocumentsByUserForGuest(foundCategory, foundField, userUploaded, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents by user successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu của một trường",
            description = "Trả về danh sách tất cả tài liệu thuộc về một trường cho quản lý")
    @GetMapping("/organizations/{organization}")
    public ResponseEntity<?> getAllDocumentsByOrganization(@PathVariable String organization,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "20") int size,
                                                           @RequestParam(defaultValue = "updatedAt") String order,
                                                           @RequestParam(defaultValue = "all") String category,
                                                           @RequestParam(defaultValue = "all") String field,
                                                           @RequestParam(defaultValue = "all") String deleted,
                                                           @RequestParam(defaultValue = "all") String internal,
                                                           @RequestParam(defaultValue = "all") String status) {

        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Boolean isDeleted = deleted.equals("all") ?
                null : Boolean.valueOf(deleted);

        Boolean isInternal = internal.equals("all") ?
                null : Boolean.valueOf(internal);

        Integer verifiedStatus = status.equals("all") ?
                null : Integer.valueOf(status);

        Page<Document> documents = documentService.findAllDocumentsWithFilter(isDeleted, isInternal, verifiedStatus, foundCategory, foundField, foundOrganization, pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get all documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu cho người dùng khách",
            description = "Trả về danh sách tài liệu công khai (internal = true)")
    @GetMapping("/public")
    public ResponseEntity<?> getDocumentsForGuests(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size,
                                                   @RequestParam(defaultValue = "updatedAt") String order,
                                                   @RequestParam(defaultValue = "desc") String sortOrder,
                                                   @RequestParam(defaultValue = "all") String category,
                                                   @RequestParam(defaultValue = "all") String field,
                                                   @RequestParam(defaultValue = "all") String organization) {
        // Order maybe one of these: totalView, updatedAt, averageRating, totalFavorite
        // Sort order maybe one of these: asc, desc
        Sort sort = Sort.by(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Organization foundOrganization = organization.equals("all") ?
                null : organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Page<Document> documents = documentService.findDocumentsForGuests(foundCategory, foundField, foundOrganization, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Tìm kiếm tài liệu cho người dùng khách",
            description = "Trả về danh sách tài liệu công khai người dùng khách tìm kiếm")
    @GetMapping("/public/search")
    public ResponseEntity<?> searchDocumentsForGuests(@RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "20") int size,
                                                      @RequestParam(defaultValue = "updatedAt") String order,
                                                      @RequestParam(defaultValue = "desc") String sortOrder,
                                                      @RequestParam(defaultValue = "all") String category,
                                                      @RequestParam(defaultValue = "all") String field,
                                                      @RequestParam(defaultValue = "all") String organization,
                                                      @RequestParam String s) {
        // Order maybe one of these: totalView, updatedAt, averageRating, totalFavorite
        // Sort order maybe one of these: asc, desc
        Sort sort = Sort.by(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Organization foundOrganization = organization.equals("all") ?
                null : organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Page<Document> documents = documentService.searchDocumentsForGuests(foundCategory, foundField, foundOrganization, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents for students successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu đang chờ duyệt",
            description = "Trả về danh sách tài liệu đang chờ duyệt cho manager hoặc admin")
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingDocuments(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Document> documents;

        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        // By organization
        if (user.getRole().getRoleName().equals("ROLE_MANAGER")) {
            Organization foundOrganization = user.getOrganization();
            documents = documentService.findByOrganizationAndVerifiedStatusAndIsDeleted(foundOrganization, 0, false, pageable);
        } else { // Not include organization
            documents = documentService.findByVerifiedStatusAndIsDeleted(0, false, pageable);
        }
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents for guest successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu cho sinh viên",
            description = "Trả về danh sách tài liệu công khai và tài liệu nội bộ thuộc truờng mà sinh viên đăng ký")
    @GetMapping("/students")
    public ResponseEntity<?> getDocumentsForStudent(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size,
                                                    @RequestParam(defaultValue = "updatedAt") String order,
                                                    @RequestParam(defaultValue = "desc") String sortOrder,
                                                    @RequestParam(defaultValue = "all") String category,
                                                    @RequestParam(defaultValue = "all") String field,
                                                    @RequestParam(defaultValue = "all") String organization) {
        // Find user info and organization
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));
        Organization userOrganization = user.getOrganization();

        // Order maybe one of these: totalView, updatedAt, averageRating, totalFavorite
        // Sort order maybe one of these: asc, desc
        Sort sort = Sort.by(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Organization foundOrganization = organization.equals("all") ?
                null : organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Page<Document> documents = documentService.findDocumentsForStudents(foundCategory, foundField, foundOrganization, userOrganization, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents for students successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Tìm kiếm tài liệu cho sinh viên",
            description = "Trả về danh sách tài liệu công khai và tài liệu nội bộ mà sinh viên tìm kiếm")
    @GetMapping("/students/search")
    public ResponseEntity<?> searchDocumentsForStudents(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size,
                                                        @RequestParam(defaultValue = "updatedAt") String order,
                                                        @RequestParam(defaultValue = "desc") String sortOrder,
                                                        @RequestParam(defaultValue = "all") String category,
                                                        @RequestParam(defaultValue = "all") String field,
                                                        @RequestParam(defaultValue = "all") String organization,
                                                        @RequestParam String s) {
        // Find user info and organization
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));
        Organization userOrganization = user.getOrganization();

        // Order maybe one of these: totalView, updatedAt, averageRating, totalFavorite
        // Sort order maybe one of these: asc, desc
        Sort sort = Sort.by(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Organization foundOrganization = organization.equals("all") ?
                null : organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Page<Document> documents = documentService.searchDocumentsForStudents(foundCategory, foundField, foundOrganization, userOrganization, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents for students successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Tạo mới một tài liệu",
            description = "Trả về tài liệu vừa mới tạo. Tuỳ vào vai trò sẽ trả về trạng thái kèm theo.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadDocument(@RequestPart("document") DocumentRequestModel documentRequestModel,
                                            @RequestPart("file") MultipartFile multipartFile) {
        // Find user info
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(auth.getPrincipal());
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Email is not valid"));
        // Get data
        Document document = modelMapper.map(documentRequestModel, Document.class);
        // Upload file document
        FileModel gd = googleDriveUpload.uploadFile(multipartFile, documentRequestModel.getDocName(), null, null);
        // Find category and field
        Category category = categoryService.findById(documentRequestModel.getCategoryId()).orElse(null);
        Field field = fieldService.findById(documentRequestModel.getFieldId()).orElse(null);
        Organization organization = organizationService.findById(documentRequestModel.getOrgId()).orElse(null);
        // Update file properties for document without overwriting existing properties
        modelMapper.map(gd, document);
        document.setSlug(slugGenerator.generateSlug(document.getDocName().replace(".pdf", ""), true));
        document.setUserUploaded(user);
        document.setCategory(category);
        document.setField(field);
        document.setOrganization(organization);
        // Student must wait for the approval
        if (user.getRole().getRoleName().equals("ROLE_STUDENT")) {
            document.setVerifiedStatus(0);
        } else {
            document.setVerifiedStatus(1);
            document.setUserVerified(user);
        }
        // Save
        documentService.save(document);
        // Map to model to return
        DocumentResponseModel savedDocument = modelMapper.map(document, DocumentResponseModel.class);
        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Upload document successfully." +
                                (user.getRole().getRoleName().equals("ROLE_STUDENT") ? " Document is waiting for approval." : ""))
                        .data(savedDocument)
                        .build());
    }

    @Operation(summary = "Cập nhật một tài liệu",
            description = "Trả về tài liệu vừa cập nhật. Tuỳ vào vai trò sẽ trả về trạng thái kèm theo.")
    @PutMapping(path = "/{slug}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDocument(@PathVariable("slug") String slug,
                                            @RequestPart("document") DocumentRequestModel documentRequestModel,
                                            @RequestPart(name = "file", required = false) MultipartFile multipartFile) {
        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        // Find document
        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found!"));
        document.setDocName(documentRequestModel.getDocName());
        document.setDocIntroduction(documentRequestModel.getDocIntroduction());
        document.setInternal(documentRequestModel.isInternal());
        //document.setSlug(slugGenerator.generateSlug(document.getDocName().replace(".pdf", ""), true));
        if (multipartFile != null) {
            // Get id of old file and thumbnail file
            String fileId = document.getViewUrl() != null ? stringHandler.getFileId(document.getViewUrl()) : null;
            String thumbnailId = document.getThumbnail() != null ? stringHandler.getFileId(document.getThumbnail()) : null;
            // Upload file
            FileModel gd = googleDriveUpload.uploadFile(multipartFile, documentRequestModel.getDocName(), fileId, thumbnailId);
            // Update file properties for document without overwriting existing properties
            document.setThumbnail(gd.getThumbnail());
            document.setViewUrl(gd.getViewUrl());
            document.setDownloadUrl(gd.getDownloadUrl());
        }
        // Find category, field and organization
        Category category = categoryService.findById(documentRequestModel.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        Field field = fieldService.findById(documentRequestModel.getFieldId()).orElseThrow(() -> new RuntimeException("Field not found"));
        Organization organization = organizationService.findById(documentRequestModel.getOrgId()).orElseThrow(() -> new RuntimeException("Organization not found"));
        document.setCategory(category);
        document.setField(field);
        document.setOrganization(organization);
        // Student must wait for the approval
        if (user.getRole().getRoleName().equals("ROLE_STUDENT")) {
            document.setVerifiedStatus(0);
        } else {
            document.setVerifiedStatus(1);
            document.setUserVerified(user);
        }
        // Save
        document = documentService.save(document);
        // Map to model to return
        DocumentResponseModel savedDocument = modelMapper.map(document, DocumentResponseModel.class);
        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Update successfully." +
                                (user.getRole().getRoleName().equals("ROLE_STUDENT") ? " Document is waiting for approval." : ""))
                        .data(savedDocument)
                        .build());
    }

    @Operation(summary = "Xoá một tài liệu",
            description = "Xoá một tài liệu")
    @DeleteMapping("/{docId}")
    public ResponseEntity<?> deleteDocument(@PathVariable UUID docId) {
        Document document = documentService.findById(docId).orElseThrow(() -> new RuntimeException("Document not found!"));
        googleDriveUpload.deleteFile(stringHandler.getFileId(document.getThumbnail()));
        googleDriveUpload.deleteFile(stringHandler.getFileId(document.getViewUrl()));
        documentService.deleteById(docId);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .message("Delete document successfully")
                .error(false)
                .build());
    }

    @Operation(summary = "Phê duyệt một tài liệu",
            description = "Phê duyệt hoặc từ chối tài liệu do sinh viên tải lên")
    @PutMapping("/{docId}/approval")
    public ResponseEntity<?> approveDocument(@PathVariable UUID docId,
                                             @RequestParam boolean isApproved,
                                             @RequestParam(required = false) String note) {
        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));
        // Find document
        Document document = documentService.findById(docId).orElseThrow(() -> new RuntimeException("Document not found!"));
        if (isApproved) {
            document.setVerifiedStatus(1);
        } else {
            document.setVerifiedStatus(-1);
            document.setNote(note);
        }
        document.setUserVerified(user);
        documentService.save(document);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .message((isApproved ? "Approve" : "Reject") + " document successfully")
                .error(false)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu mới tải lên trong tháng",
            description = "Trả về danh sách tài liệu được tải lên trong tháng trên hệ thống")
    @GetMapping("/latest")
    public ResponseEntity<?> getLatestDocuments(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size,
                                                @RequestParam(defaultValue = "updatedAt") String order,
                                                @RequestParam(defaultValue = "all") String category,
                                                @RequestParam(defaultValue = "all") String field,
                                                @RequestParam(defaultValue = "all") String organization,
                                                @RequestParam(defaultValue = "all") String deleted,
                                                @RequestParam(defaultValue = "all") String internal,
                                                @RequestParam(defaultValue = "all") String status) {

        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Organization foundOrganization = organization.equals("all") ?
                null : organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Boolean isDeleted = deleted.equals("all") ?
                null : Boolean.valueOf(deleted);

        Boolean isInternal = internal.equals("all") ?
                null : Boolean.valueOf(internal);

        Integer verifiedStatus = status.equals("all") ?
                null : Integer.valueOf(status);

        Page<Document> documents = documentService.findLatestDocuments(isDeleted, isInternal, verifiedStatus, foundCategory, foundField, foundOrganization, pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get latest documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu mới tải lên trong tháng của một trường",
            description = "Trả về danh sách tài liệu được tải lên trong tháng của một trường trên hệ thống")
    @GetMapping("/organizations/{organization}/latest")
    public ResponseEntity<?> getLatestDocumentsByOrganization(@PathVariable String organization,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "5") int size,
                                                              @RequestParam(defaultValue = "updatedAt") String order,
                                                              @RequestParam(defaultValue = "all") String category,
                                                              @RequestParam(defaultValue = "all") String field,
                                                              @RequestParam(defaultValue = "all") String deleted,
                                                              @RequestParam(defaultValue = "all") String internal,
                                                              @RequestParam(defaultValue = "all") String status) {

        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));

        Category foundCategory = category.equals("all") ?
                null : categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));

        Field foundField = field.equals("all") ?
                null : fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));

        Boolean isDeleted = deleted.equals("all") ?
                null : Boolean.valueOf(deleted);

        Boolean isInternal = internal.equals("all") ?
                null : Boolean.valueOf(internal);

        Integer verifiedStatus = status.equals("all") ?
                null : Integer.valueOf(status);

        Page<Document> documents = documentService.findLatestDocuments(isDeleted, isInternal, verifiedStatus, foundCategory, foundField, foundOrganization, pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get latest documents successfully")
                .data(documentModels)
                .build());
    }

    private DocumentResponseModel convertToDocumentModel(Object o) {
        DocumentResponseModel documentResponseModel = modelMapper.map(o, DocumentResponseModel.class);
        return documentResponseModel;
    }
}
