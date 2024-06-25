package com.major_project.digital_library.controller;

import com.major_project.digital_library.model.CollectionLikeModel;
import com.major_project.digital_library.model.request_model.CollectionRequestModel;
import com.major_project.digital_library.model.response_model.CollectionResponseModel;
import com.major_project.digital_library.model.response_model.DetailCollectionResponseModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.ICollectionDocumentService;
import com.major_project.digital_library.service.ICollectionLikeService;
import com.major_project.digital_library.service.ICollectionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v2/collections")
public class CollectionController {
    private final ICollectionService collectionService;
    private final ICollectionDocumentService collectionDocumentService;
    private final ICollectionLikeService collectionLikeService;

    @Autowired
    public CollectionController(ICollectionService collectionService, ICollectionDocumentService collectionDocumentService, ICollectionLikeService collectionLikeService) {
        this.collectionService = collectionService;
        this.collectionDocumentService = collectionDocumentService;
        this.collectionLikeService = collectionLikeService;
    }

    @Operation(summary = "Lấy danh sách bộ sưu tập cho tất cả mọi người")
    @GetMapping("/public")
    public ResponseEntity<?> getPublicCollections(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  @RequestParam(defaultValue = "") String s) {
        Page<CollectionResponseModel> collectionResponseModels = collectionService.getPublicCollections(page, size, s);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get public collections successfully")
                .data(collectionResponseModels)
                .build());
    }

    @Operation(summary = "Lấy danh sách bộ sưu tập cho người dùng đã đăng nhập")
    @GetMapping
    public ResponseEntity<?> getCollectionsForUser(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size,
                                                   @RequestParam(defaultValue = "") String s) {
        Page<CollectionResponseModel> collectionResponseModels = collectionService.getCollectionsForUser(page, size, s);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get collections for user successfully")
                .data(collectionResponseModels)
                .build());
    }

    @Operation(summary = "Lấy danh sách bộ sưu tập người dùng hiện tại")
    @GetMapping("/mine")
    public ResponseEntity<?> getCollectionsOfUser(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "8") int size,
                                                  @RequestParam(defaultValue = "") String s) {
        Page<CollectionResponseModel> collectionResponseModels = collectionService.getCollectionsOfUser(page, size, s);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Get collections of current user successfully")
                .data(collectionResponseModels)
                .build());
    }

    @Operation(summary = "Xem chi tiết bộ sưu tập")
    @GetMapping("/{slug}")
    public ResponseEntity<?> getDetailCollection(@PathVariable String slug,
                                                 @RequestParam(defaultValue = "") String s) {
        DetailCollectionResponseModel collectionResponseModel = collectionService.getDetailCollection(slug, s);

        if (collectionResponseModel == null)
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(false)
                    .message("Collection not accessible")
                    .build());
        else
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(200)
                    .error(false)
                    .message("Get detail collection successfully")
                    .data(collectionResponseModel)
                    .build());
    }

    @Operation(summary = "Xem chi tiết bộ sưu tập cho khách")
    @GetMapping("/{slug}/public")
    public ResponseEntity<?> getDetailCollectionForGuest(@PathVariable String slug,
                                                         @RequestParam(defaultValue = "") String s) {
        DetailCollectionResponseModel collectionResponseModel = collectionService.getDetailCollectionForGuest(slug, s);

        if (collectionResponseModel == null)
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(404)
                    .error(false)
                    .message("Collection not accessible")
                    .build());
        else
            return ResponseEntity.ok(ResponseModel
                    .builder()
                    .status(200)
                    .error(false)
                    .message("Get detail collection successfully")
                    .data(collectionResponseModel)
                    .build());
    }

    @Operation(summary = "Tạo bộ sưu tập")
    @PostMapping
    public ResponseEntity<?> addCollection(@RequestBody CollectionRequestModel collectionRequestModel) {
        CollectionResponseModel collectionResponseModel = collectionService.addCollection(collectionRequestModel);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Add collection successfully")
                .data(collectionResponseModel)
                .build());
    }

    @Operation(summary = "Chỉnh sửa bộ sưu tập")
    @PutMapping("/{collectionId}")
    public ResponseEntity<?> editCollection(@PathVariable UUID collectionId,
                                            @RequestBody CollectionRequestModel collectionRequestModel) {
        CollectionResponseModel collectionResponseModel = collectionService.editCollection(collectionId, collectionRequestModel);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Edit collection successfully")
                .data(collectionResponseModel)
                .build());
    }

    @Operation(summary = "Xoá bộ sưu tập")
    @DeleteMapping("/{collectionId}")
    public ResponseEntity<?> removeCollection(@PathVariable UUID collectionId) {
        collectionService.removeCollection(collectionId);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Remove collection successfully")
                .build());
    }

    @Operation(summary = "Thêm tài liệu vào bộ sưu tập")
    @PostMapping("/{collectionId}/document/{docId}")
    public ResponseEntity<?> addToCollection(@PathVariable UUID collectionId,
                                             @PathVariable UUID docId) {
        CollectionResponseModel collectionResponseModel = collectionDocumentService.addToCollection(collectionId, docId);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Add document to collection successfully")
                .data(collectionResponseModel)
                .build());
    }

    @Operation(summary = "Xoá tài liệu khỏi bộ sưu tập")
    @DeleteMapping("/{collectionId}/document/{docId}")
    public ResponseEntity<?> removeFromCollection(@PathVariable UUID collectionId,
                                                  @PathVariable UUID docId) {
        CollectionResponseModel collectionResponseModel = collectionDocumentService.removeFromCollection(collectionId, docId);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Remove document from collection successfully")
                .data(collectionResponseModel)
                .build());
    }

    @Operation(summary = "Thích một bộ sưu tập",
            description = "Thêm một bộ sưu tập vào danh sách đã thích")
    @PostMapping("/{collectionId}/like")
    public ResponseEntity<?> likeCollection(@PathVariable UUID collectionId) {
        collectionLikeService.likeCollection(collectionId);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Like collection successfully")
                .build());
    }

    @Operation(summary = "Bỏ thích một bộ sưu tập",
            description = "Xoá một bộ sưu tập khỏi danh sách đã thích")
    @PostMapping("/{collectionId}/unlike")
    public ResponseEntity<?> unlikeCollection(@PathVariable UUID collectionId) {
        CollectionLikeModel collectionLikeModel = collectionLikeService.unlikeCollection(collectionId);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Unlike collection successfully")
                .data(collectionLikeModel)
                .build());
    }

    @Operation(summary = "Hoàn tác bỏ thích một bộ sưu tập",
            description = "Thêm bộ sưu tập vào lại danh sách đã thích")
    @PostMapping("/{collectionId}/relike")
    public ResponseEntity<?> undoUnlikeCollection(@PathVariable UUID collectionId, @RequestBody CollectionLikeModel collectionLikeModel) {
        collectionLikeService.undoUnlike(collectionId, collectionLikeModel);

        return ResponseEntity.ok(ResponseModel
                .builder()
                .status(200)
                .error(false)
                .message("Relike collection successfully")
                .build());
    }

    @Operation(summary = "Xem danh sách đã thích",
            description = "Trả về danh sách bộ sưu tập đã thích")
    @GetMapping("/liked")
    public ResponseEntity<?> getFavoriteCollections(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "12") int size,
                                                    @RequestParam String s) {
        Page<CollectionResponseModel> collectionModels = collectionLikeService.getLikedCollections(page, size, s);

        return ResponseEntity.ok(ResponseModel.builder()
                .error(false)
                .status(200)
                .message("Get liked collections successfully")
                .data(collectionModels)
                .build());
    }
}
