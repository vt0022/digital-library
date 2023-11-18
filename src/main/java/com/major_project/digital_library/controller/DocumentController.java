package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.request_model.DocumentRequestModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.*;
import com.major_project.digital_library.util.GoogleDriveUpload;
import com.major_project.digital_library.util.SlugGenerator;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final GoogleDriveUpload googleDriveUpload;
    private final IDocumentService documentService;
    private final ICategoryService categoryService;
    private final IFieldService fieldService;
    private final IOrganizationService organizationService;
    private final IUserService userService;
    private final ModelMapper modelMapper;
    private final SlugGenerator slugGenerator;

    @Autowired
    public DocumentController(GoogleDriveUpload googleDriveUpload, IDocumentService documentService, ICategoryService categoryService, IFieldService fieldService, IOrganizationService organizationService, IUserService userService, ModelMapper modelMapper, SlugGenerator slugGenerator) {
        this.googleDriveUpload = googleDriveUpload;
        this.documentService = documentService;
        this.categoryService = categoryService;
        this.fieldService = fieldService;
        this.organizationService = organizationService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.slugGenerator = slugGenerator;
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<?> uploadFileToGoogleDrive(
            @RequestParam("file") MultipartFile multipartFile) {
        String name = multipartFile.getName();
        FileModel gd = googleDriveUpload.uploadFile(multipartFile, name);
        Document doc = new Document();
        doc = modelMapper.map(gd, Document.class);
        doc.setSlug(slugGenerator.generateSlug(doc.getDocName().replace(".pdf", ""), true));

        doc.setCategory(categoryService.findBySlug("giao-trinh").get());
        doc.setField(fieldService.findBySlug("nghe-thuat-am-thuc").get());

        documentService.save(doc);
        return ResponseEntity.ok(doc);
    }

    /* API endpoint này được dùng để xem một tài liệu */
    @Operation(summary = "Xem chi tiết một tài liệu")
    @GetMapping("/{slug}")
    public ResponseEntity<?> viewDocument(@PathVariable String slug) {
        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Could not find document"));
        // Increase views
        document.setTotalView(document.getTotalView() + 1);
        documentService.save(document);
        DocumentResponseModel documentResponseModel = modelMapper.map(document, DocumentResponseModel.class);
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
    public ResponseEntity<?> getAllDocuments(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(defaultValue = "docId") String order) {
        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(order.equals("docId") ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);
//        List<Document> documents = documentService.findAll(pageable).getContent();
//        List<DocumentModel> documentModels = documents
//                .stream()
//                .map(document -> modelMapper.map(document, DocumentModel.class))
//                .collect(Collectors.toList());
        Page<Document> documents = documentService.findAll(pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get document successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu đã tải lên của một người",
            description = "Trả về danh sách tất cả tài liệu mà một người đã tải lên")
    @GetMapping("/mine")
    public ResponseEntity<?> getDocumentsByUser(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size,
                                                @RequestParam(defaultValue = "docId") String order) {
        // Find user info
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(auth.getPrincipal());
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Email is not valid"));
        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(order.equals("docId") ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Document> documents = documentService.findByUserUploadedAndIsDeleted(user, false, pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get uploaded documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu của một trường",
            description = "Trả về danh sách tất cả tài liệu thuộc về một trường")
    @GetMapping("/organizations/{organization}")
    public ResponseEntity<?> getDocumentsByOrganization(@PathVariable String organization,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size,
                                                        @RequestParam(defaultValue = "docId") String order) {
        // Find organization
        Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Could not find organization"));
        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(order.equals("docId") ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Document> documents = documentService.findAllByOrganization(foundOrganization, pageable);
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get document successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu cho người dùng ẩn danh",
            description = "Trả về danh sách tài liệu công khai (internal = true)")
    @GetMapping("/public")
    public ResponseEntity<?> getAccessibleDocument(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size,
                                                   @RequestParam(defaultValue = "docId") String order,
                                                   @RequestParam(defaultValue = "all") String category,
                                                   @RequestParam(defaultValue = "all") String field,
                                                   @RequestParam(defaultValue = "all") String organization) {
        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(order.equals("docId") ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Document> documents = documentService.findByVerifiedStatusAndIsInternalAndIsDeleted(1, false, false, pageable);

        // By category
        if (!category.equals("all")) {
            Category foundCategory = categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Could not find category"));
            // By field
            if (!field.equals("all")) {
                Field foundField = fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Could not find field"));
                // By organization
                if (!organization.equals("all")) {
                    Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Could not find organization"));
                    documents = documentService.findByCategoryAndFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(foundCategory, foundField, foundOrganization, 1, false, false, pageable);
                } else { // Not include organization
                    documents = documentService.findByCategoryAndFieldAndVerifiedStatusAndIsInternalAndIsDeleted(foundCategory, foundField, 1, false, false, pageable);
                }
            } else { // Not include field
                // By organization
                if (!organization.equals("all")) {
                    Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Could not find organization"));
                    documents = documentService.findByCategoryAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(foundCategory, foundOrganization, 1, false, false, pageable);
                } else { // Not include organization
                    documents = documentService.findByCategoryAndVerifiedStatusAndIsInternalAndIsDeleted(foundCategory, 1, false, false, pageable);
                }
            }
        } else { // Not include category
            // By field
            if (!field.equals("all")) {
                Field foundField = fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Could not find field"));
                // By organization
                if (!organization.equals("all")) {
                    Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Could not find organization"));
                    documents = documentService.findByFieldAndOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(foundField, foundOrganization, 1, false, false, pageable);
                } else { // Not included
                    documents = documentService.findByFieldAndVerifiedStatusAndIsInternalAndIsDeleted(foundField, 1, false, false, pageable);
                }
            } else {
                if (!organization.equals("all")) {
                    Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Could not find organization"));
                    documents = documentService.findByOrganizationAndVerifiedStatusAndIsInternalAndIsDeleted(foundOrganization, 1, false, false, pageable);
                }
            }
        }
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu đang chờ duyệt",
            description = "Trả về danh sách tài liệu đang chờ duyệt cho manager hoặc admin")
    @GetMapping("/waiting")
    public ResponseEntity<?> getWaitingDocument(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size) {

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Document> documents;

        // Find user info
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(auth.getPrincipal());
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Email is not valid"));

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
                .message("Get documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu cho sinh viên",
            description = "Trả về danh sách tài liệu công khai và tài liệu nội bộ thuộc truờng mà sinh viên đăng ký")
    @GetMapping("/students")
    public ResponseEntity<?> getFilteredDocument(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size,
                                                 @RequestParam(defaultValue = "docId") String order,
                                                 @RequestParam(defaultValue = "all") String category,
                                                 @RequestParam(defaultValue = "all") String field,
                                                 @RequestParam(defaultValue = "all") String organization
    ) {
        // Find user info
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(auth.getPrincipal());
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Email is not valid"));
        Organization userOrganization = user.getOrganization();
        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(order.equals("docId") ? Sort.Direction.ASC : Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Document> documents = documentService.findByInternal(userOrganization, pageable);

        // By category
        if (!category.equals("all")) {
            Category foundCategory = categoryService.findBySlug(category).orElseThrow(() -> new RuntimeException("Could not find category"));
            // By field
            if (!field.equals("all")) {
                Field foundField = fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Could not find field"));
                // By organization
                if (!organization.equals("all")) {
                    Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Could not find organization"));
                    documents = documentService.findByCategoryAndFieldAndOrganization(foundCategory, foundField, foundOrganization, userOrganization, pageable);
                } else { // Not include organization
                    documents = documentService.findByCategoryAndField(foundCategory, foundField, userOrganization, pageable);
                }
            } else { // Not include field
                // By organization
                if (!organization.equals("all")) {
                    Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Could not find organization"));
                    documents = documentService.findByCategoryAndOrganization(foundCategory, foundOrganization, userOrganization, pageable);
                } else { // Not include organization
                    documents = documentService.findByCategory(foundCategory, userOrganization, pageable);
                }
            }
        } else { // Not include category
            // By field
            if (!field.equals("all")) {
                Field foundField = fieldService.findBySlug(field).orElseThrow(() -> new RuntimeException("Could not find field"));
                // By organization
                if (!organization.equals("all")) {
                    Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Could not find organization"));
                    documents = documentService.findByFieldAndOrganization(foundField, foundOrganization, userOrganization, pageable);
                } else { // Not included
                    documents = documentService.findByField(foundField, userOrganization, pageable);
                }
            } else {
                if (!organization.equals("all")) {
                    Organization foundOrganization = organizationService.findBySlug(organization).orElseThrow(() -> new RuntimeException("Could not find organization"));
                    documents = documentService.findByOrganization(foundOrganization, userOrganization, pageable);
                }
            }
        }
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents successfully")
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
        // Upload filedocument
        FileModel gd = googleDriveUpload.uploadFile(multipartFile, documentRequestModel.getDocName());
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

    @Transactional
    @Operation(summary = "Cập nhật một tài liệu",
            description = "Trả về tài liệu vừa cập nhật. Tuỳ vào vai trò sẽ trả về trạng thái kèm theo.")
    @PutMapping(path = "/{slug}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDocument(@PathVariable("slug") String slug,
                                            @RequestPart("document") DocumentRequestModel documentRequestModel,
                                            @RequestPart(name = "file", required = false) MultipartFile multipartFile) {
        // Find user info
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(auth.getPrincipal());
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Email is not valid"));
        // Find document
        Document document = documentService.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found!"));

        modelMapper.map(documentRequestModel, document);
        if (multipartFile != null) {
            // Upload file
            FileModel gd = googleDriveUpload.uploadFile(multipartFile, documentRequestModel.getDocName());
            // Update file properties for document without overwriting existing properties
            document = modelMapper.map(gd, Document.class);
            document.setSlug(slugGenerator.generateSlug(document.getDocName().replace(".pdf", ""), true));
        }
        // Find category and field
        Category category = categoryService.findById(documentRequestModel.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        Field field = fieldService.findById(documentRequestModel.getFieldId()).orElseThrow(() -> new RuntimeException("Field not found"));
        Organization organization = organizationService.findById(documentRequestModel.getOrgId()).orElseThrow(() -> new RuntimeException("Organization not found"));
        document.setCategory(category);
        document.setField(new Field());
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
            description = "Xoá một tài liệu (xoá mềm)")
    @DeleteMapping("/{docId}")
    public ResponseEntity<?> deleteDocument(@PathVariable UUID docId) {
        Document document = documentService.findById(docId).orElseThrow(() -> new RuntimeException("Document not found!"));
        document.setDeleted(true);
        documentService.save(document);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .message("Delete document successfully")
                .error(false)
                .build());
    }

    @Operation(summary = "Xoá một tài liệu vĩnh viễn",
            description = "Xoá một tài liệu (xoá cứng)")
    @DeleteMapping("/{docId}/permanent")
    public ResponseEntity<?> deleteDocumentPermanently(@PathVariable UUID docId) {
        documentService.deleteById(docId);
        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .message("Delete document from system successfully")
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
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String email = String.valueOf(auth.getPrincipal());
        User user = userService.findByEmail(email).orElseThrow(() -> new RuntimeException("Email is not valid"));
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


//    @PostMapping("/update")
//    public ResponseEntity<?> uploadFileToGoogleDrive() {
//        List<Document> documents = documentService.findAll();
//        for (Document doc : documents) {
//            //doc.setOrganization(organizationService.findById(UUID.fromString("c0a801b9-8ac0-1a60-818a-c04a8ea90019")).get());
//            //doc.setVerified(true);
//            Random random = new Random();
//            // Generate a random integer between 10 and 200 (inclusive)
//            int randomNumber = random.nextInt(181) + 20;
//            doc.setTotalView(randomNumber);
//            doc.setTotalFavorite(random.nextInt(15));
//            documentService.save(doc);
//        }
//
//        return ResponseEntity.ok().build();
//    }
//
//    @GetMapping("/test")
//    public ResponseEntity<?> test() {
//        Pageable pageable = PageRequest.of(0, 10);
//        List<Category> categorySet = categoryService.findAll(pageable).getContent();
//        List<CategoryModel> categoryModelSet = modelMapper.map(categorySet, new TypeToken<List<CategoryModel>>() {
//        }.getType());
//        return ResponseEntity.ok(categoryModelSet);
////        Set<Document> documentSet = new HashSet<>(documentService.findAll(pageable).getContent());
////        Set<DocumentModel> documentModelSet = modelMapper.map(documentSet, new TypeToken<Set<DocumentModel>>() {
////        }.getType());
////        return ResponseEntity.ok(categoryService.findAll(pageable).getContent());
//    }

//    @GetMapping("/test")
//    public ResponseEntity<?> test(@RequestPart("document") DocumentModel documentModel,
//                                  @RequestPart("file") MultipartFile multipartFile) {
//        // Get data
//        Document document = modelMapper.map(documentModel, Document.class);
//        // Upload file
//        FileModel gd = googleDriveUpload.uploadFile(multipartFile, documentModel.getDocName());
//        // Find category
//        Category category = categoryService.findById(documentModel.getCategory().getCategoryId()).orElse(null);
//        Field field = fieldService.findById(documentModel.getField().getFieldId()).orElse(null);
//        return ResponseEntity.ok(document);
//    }


    @GetMapping("/generateSlugForOrg")
    public ResponseEntity<?> test() {
        Pageable pageable = PageRequest.of(0, 100);
        List<Organization> organizationList = organizationService.findAll(pageable).getContent();
        for (Organization organization : organizationList) {
            organization.setSlug(SlugGenerator.generateSlug(organization.getOrgName(), false));
            organizationService.save(organization);
        }
        return ResponseEntity.ok("Completed");
    }

    private DocumentResponseModel convertToDocumentModel(Object o) {
        DocumentResponseModel documentResponseModel = modelMapper.map(o, DocumentResponseModel.class);
        return documentResponseModel;
    }

}
