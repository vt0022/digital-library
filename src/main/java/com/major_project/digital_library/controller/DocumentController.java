package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.DocumentLikeModel;
import com.major_project.digital_library.model.SaveModel;
import com.major_project.digital_library.model.request_model.DocumentRequestModel;
import com.major_project.digital_library.model.response_model.DetailDocumentResponseModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.IDocumentLikeService;
import com.major_project.digital_library.service.IDocumentService;
import com.major_project.digital_library.service.IRecencyService;
import com.major_project.digital_library.service.ISaveService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/documents")
public class DocumentController {
    private final IDocumentService documentService;
    private final IDocumentLikeService documentLikeService;
    private final ISaveService saveService;
    private final IRecencyService recencyService;

    @Autowired
    public DocumentController(IDocumentService documentService, IDocumentLikeService documentLikeService, ISaveService saveService, IRecencyService recencyService) {
        this.documentService = documentService;
        this.documentLikeService = documentLikeService;
        this.saveService = saveService;
        this.recencyService = recencyService;
    }

    @Operation(summary = "Xem chi tiết một tài liệu cho sinh viên, quản lý, admin")
    @GetMapping("/{slug}")
    public ResponseEntity<?> viewDocument(@PathVariable String slug) {
        DetailDocumentResponseModel detailDocumentResponseModel = documentService.viewDocument(slug);

        if (detailDocumentResponseModel == null)
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(true)
                    .message("Document not accessible")
                    .build());
        else
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(200)
                    .error(false)
                    .message("Get document successfully")
                    .data(detailDocumentResponseModel)
                    .build());
    }

    @Operation(summary = "Xem chi tiết một tài liệu cho khách")
    @GetMapping("/{slug}/public")
    public ResponseEntity<?> viewDocumentForGuest(@PathVariable String slug) {
        DetailDocumentResponseModel detailDocumentResponseModel = documentService.viewDocumentForGuest(slug);

        if (detailDocumentResponseModel == null)
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(true)
                    .message("Document not accessible")
                    .build());
        else
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(200)
                    .error(false)
                    .message("Get document successfully")
                    .data(detailDocumentResponseModel)
                    .build());
    }

    @Operation(summary = "Xem danh sách toàn bộ tài liệu",
            description = "Trả về danh sách tất cả tài liệu trên hệ thống (kèm lọc và tìm kiếm)")
    @GetMapping()
    public ResponseEntity<?> getAllDocuments(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(defaultValue = "updatedAt") String order,
                                             @RequestParam(defaultValue = "all") String category,
                                             @RequestParam(defaultValue = "all") String field,
                                             @RequestParam(defaultValue = "all") String organization,
                                             @RequestParam(defaultValue = "all") String deleted,
                                             @RequestParam(defaultValue = "all") String internal,
                                             @RequestParam(defaultValue = "all") String status,
                                             @RequestParam(defaultValue = "") String s) {
        Page<DocumentResponseModel> documentModels = documentService.getAllDocuments(page, size, order, category, field, organization, deleted, internal, status, s);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get all documents successfully")
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
                                                           @RequestParam(defaultValue = "all") String status,
                                                           @RequestParam(defaultValue = "") String s) {
        Page<DocumentResponseModel> documentModels = documentService.getAllDocuments(page, size, order, category, field, organization, deleted, internal, status, s);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents by organization successfully")
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
                                          @RequestParam(defaultValue = "") String s) {
        Page<DocumentResponseModel> documentModels = documentService.getMyUploads(page, size, order, category, organization, field, status, s);

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
        Page<DocumentResponseModel> documentModels = documentService.getOwnedDocuments(page, size);

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
        Page<DocumentResponseModel> documentModels = documentService.getDocumentsByUser(userId, page, size);

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
                                                           @RequestParam(defaultValue = "") String s) {
        Page<DocumentResponseModel> documentModels = documentService.findDocumentsByUserForStudent(userId, page, size, order, category, field, s);

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
                                                         @RequestParam(defaultValue = "") String s) {
        Page<DocumentResponseModel> documentModels = documentService.findDocumentsByUserForGuest(userId, page, size, order, category, field, s);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get documents by user successfully")
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
        Page<DocumentResponseModel> documentModels = documentService.getDocumentsForGuests(page, size, order, sortOrder, category, field, organization, "");

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
                                                      @RequestParam(defaultValue = "") String s) {
        Page<DocumentResponseModel> documentModels = documentService.getDocumentsForGuests(page, size, order, sortOrder, category, field, organization, s);

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
    public ResponseEntity<?> getPendingDocuments(@RequestParam(defaultValue = "all") String organization,
                                                 @RequestParam(defaultValue = "all") String status,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "20") int size) {
        Page<DocumentResponseModel> documentModels = documentService.getPendingDocuments(page, size, status, organization);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get pending document successfully")
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
        Page<DocumentResponseModel> documentModels = documentService.getDocumentsForStudent(page, size, order, sortOrder, category, field, organization, "");

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
                                                        @RequestParam(defaultValue = "") String s) {
        Page<DocumentResponseModel> documentModels = documentService.getDocumentsForStudent(page, size, order, sortOrder, category, field, organization, s);

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
        DocumentResponseModel documentResponseModel = documentService.uploadDocument(documentRequestModel, multipartFile);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Upload document successfully.") // (user.getRole().getRoleName().equals("ROLE_STUDENT") ? " Document is waiting for approval." : "")
                        .data(documentResponseModel)
                        .build());
    }

    @Operation(summary = "Cập nhật một tài liệu",
            description = "Trả về tài liệu vừa cập nhật. Tuỳ vào vai trò sẽ trả về trạng thái kèm theo.")
    @PutMapping(path = "/{slug}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDocument(@PathVariable("slug") String slug,
                                            @RequestPart("document") DocumentRequestModel documentRequestModel,
                                            @RequestPart(name = "file", required = false) MultipartFile multipartFile) {
        DocumentResponseModel documentResponseModel = documentService.updateDocument(slug, documentRequestModel, multipartFile);

        return ResponseEntity.ok(
                ResponseModel
                        .builder()
                        .status(200)
                        .error(false)
                        .message("Update document successfully")
                        .data(documentResponseModel)
                        .build());
    }

    @Operation(summary = "Xoá một tài liệu",
            description = "Xoá một tài liệu")
    @DeleteMapping("/{docId}")
    public ResponseEntity<?> deleteDocument(@PathVariable UUID docId) {
        documentService.deleteDocument(docId);

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
        documentService.approveDocument(docId, isApproved, note);

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
                                                @RequestParam(defaultValue = "all") String status,
                                                @RequestParam(defaultValue = "") String s) {
        Page<DocumentResponseModel> documentModels = documentService.getLatestDocuments(page, size, order, category, field, organization, deleted, internal, status, s);

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
                                                              @RequestParam(defaultValue = "all") String status,
                                                              @RequestParam(defaultValue = "") String s) {
        Page<DocumentResponseModel> documentModels = documentService.getLatestDocuments(page, size, order, category, field, organization, deleted, internal, status, s);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get latest documents of organization successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu liên quan của một tài liệu cụ thể",
            description = "Trả về danh sách tài liệu liên quan khi nhấn xem 1 tài liệu cụ thể")
    @GetMapping("/related/{slug}")
    public ResponseEntity<?> getRelatedDocument(@PathVariable String slug) {
        Page<DocumentResponseModel> documentModels = documentService.findRelatedDocuments(slug);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get related documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Xem danh sách tài liệu liên quan của một tài liệu cụ thể công khai",
            description = "Trả về danh sách tài liệu liên quan khi nhấn xem 1 tài liệu cụ thể công khai")
    @GetMapping("/related/{slug}/guest")
    public ResponseEntity<?> getRelatedDocumentForGuest(@PathVariable String slug) {
        Page<DocumentResponseModel> documentModels = documentService.findRelatedDocumentsForGuest(slug);
        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get related documents for guest successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Thích một tài liệu",
            description = "Thêm một tài liệu vào danh sách đã thích")
    @PostMapping("/{slug}/like")
    public ResponseEntity<?> likeDocument(@PathVariable String slug) {
        documentLikeService.likeDocument(slug);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Like document successfully")
                .build());
    }

    @Operation(summary = "Bỏ thích một tài liệu",
            description = "Xoá một tài liệu khỏi danh sách đã thích")
    @PostMapping("/{slug}/unlike")
    public ResponseEntity<?> unlikeDocument(@PathVariable String slug) {
        DocumentLikeModel documentLikeModel = documentLikeService.unlikeDocument(slug);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Unlike document successfully")
                .data(documentLikeModel)
                .build());
    }

    @Operation(summary = "Hoàn tác bỏ thích một tài liệu",
            description = "Thêm tài liệu vào lại danh sách đã thích")
    @PostMapping("/{slug}/relike")
    public ResponseEntity<?> undoUnlikeDocument(@PathVariable String slug, @RequestBody DocumentLikeModel documentLikeModel) {
        documentLikeService.undoUnlike(slug, documentLikeModel);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Relike document successfully")
                .build());
    }

    @Operation(summary = "Xem danh sách đã thích",
            description = "Trả về danh sách tài liệu đã thích")
    @GetMapping("/liked")
    public ResponseEntity<?> getFavoriteDocuments(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "12") int size,
                                                  @RequestParam String s) {
        Page<DocumentResponseModel> documentModels = documentLikeService.getLikedDocuments(page, size, s);

        return ResponseEntity.ok(ResponseModel.builder()
                .error(false)
                .status(200)
                .message("Get liked documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Lưu một tài liệu",
            description = "Thêm một tài liệu vào danh sách đã lưu")
    @PostMapping("/{slug}/save")
    public ResponseEntity<?> saveDocument(@PathVariable String slug) {
        saveService.saveDocument(slug);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Save document successfully")
                .build());
    }

    @Operation(summary = "Bỏ lưu một tài liệu",
            description = "Xoá một tài liệu khỏi danh sách đã lưu")
    @PostMapping("/{slug}/unsave")
    public ResponseEntity<?> unsaveDocument(@PathVariable String slug) {
        SaveModel saveModel = saveService.unsaveDocument(slug);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Unsave document successfully")
                .data(saveModel)
                .build());
    }

    @Operation(summary = "Hoàn tác bỏ lưu một tài liệu",
            description = "Thêm tài liệu vào lại danh sách đã lưu")
    @PostMapping("/{slug}/resave")
    public ResponseEntity<?> undoUnsaveDocument(@PathVariable String slug, @RequestBody SaveModel saveModel) {
        saveService.undoUnsave(slug, saveModel);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Resave document successfully")
                .build());
    }

    @Operation(summary = "Xem danh sách đã lưu",
            description = "Trả về danh sách tài liệu đã lưu")
    @GetMapping("/saved")
    public ResponseEntity<?> getSavedDocuments(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "12") int size,
                                               @RequestParam String s) {
        Page<DocumentResponseModel> documentModels = saveService.getSavedDocuments(page, size, s);
        return ResponseEntity.ok(ResponseModel.builder()
                .error(false)
                .status(200)
                .message("Get saved documents successfully")
                .data(documentModels)
                .build());
    }

    @Operation(summary = "Trả về danh sách gần đây")
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentDocuments() {
        List<DocumentResponseModel> documentResponseModels = recencyService.getRecentDocuments();

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get recent documents successfully")
                .data(documentResponseModels)
                .build());
    }
}
