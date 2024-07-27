package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.constant.NotificationMessage;
import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.request_model.DocumentRequestModel;
import com.major_project.digital_library.model.response_model.DetailDocumentResponseModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.repository.*;
import com.major_project.digital_library.service.*;
import com.major_project.digital_library.service.other.GoogleDriveService;
import com.major_project.digital_library.util.SlugGenerator;
import com.major_project.digital_library.util.StringHandler;
import com.major_project.digital_library.yake.TagExtractor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements IDocumentService {
    private final IDocumentRepository documentRepository;
    private final ICategoryRepository categoryRepository;
    private final IFieldRepository fieldRepository;
    private final IOrganizationRepository organizationRepository;
    private final IUserRepository userRepositoty;
    private final IDocumentLikeRepository documentLikeRepository;
    private final ISaveRepository saveRepository;
    private final IReviewRepository reviewRepository;
    private final ITagRepository tagRepository;
    private final IRecencyRepository recencyRepository;
    private final ICollectionRepository collectionRepository;
    private final ICollectionDocumentService collectionDocumentService;
    private final IUserService userService;
    private final IRecencyService recencyService;
    private final INotificationService notificationService;
    private final IDocumentNoteService documentNoteService;
    private final ModelMapper modelMapper;
    private final SlugGenerator slugGenerator;
    private final TagExtractor tagExtractor;
    private final GoogleDriveService googleDriveService;
    private final StringHandler stringHandler;

    @Autowired
    public DocumentServiceImpl(IDocumentRepository documentRepository, ICategoryRepository categoryRepository, IFieldRepository fieldRepository, IOrganizationRepository organizationRepository, IUserRepository userRepositoty, IDocumentLikeRepository documentLikeRepository, ISaveRepository saveRepository, IReviewRepository reviewRepository, ITagRepository tagRepository, IRecencyRepository recencyRepository, ICollectionRepository collectionRepository, ICollectionDocumentService collectionDocumentService, IUserService userService, IRecencyService recencyService, INotificationService notificationService, IDocumentNoteService documentNoteService, ModelMapper modelMapper, SlugGenerator slugGenerator, TagExtractor tagExtractor, GoogleDriveService googleDriveService, StringHandler stringHandler) {
        this.documentRepository = documentRepository;
        this.categoryRepository = categoryRepository;
        this.fieldRepository = fieldRepository;
        this.organizationRepository = organizationRepository;
        this.userRepositoty = userRepositoty;
        this.documentLikeRepository = documentLikeRepository;
        this.saveRepository = saveRepository;
        this.reviewRepository = reviewRepository;
        this.tagRepository = tagRepository;
        this.recencyRepository = recencyRepository;
        this.collectionRepository = collectionRepository;
        this.collectionDocumentService = collectionDocumentService;
        this.userService = userService;
        this.recencyService = recencyService;
        this.notificationService = notificationService;
        this.documentNoteService = documentNoteService;
        this.modelMapper = modelMapper;
        this.slugGenerator = slugGenerator;
        this.tagExtractor = tagExtractor;
        this.googleDriveService = googleDriveService;
        this.stringHandler = stringHandler;
    }

    @Override
    public DetailDocumentResponseModel viewDocument(String slug) {
        User user = userService.findLoggedInUser();
        Document document = documentRepository.findBySlug(slug).orElse(null);

        if (document == null)
            return null;

        if (user.getRole().getRoleName().equals("ROLE_STUDENT")) {
            if (user == document.getUserUploaded()) {
                if (document.getCategory().isDeleted() ||
                        document.getField().isDeleted() ||
                        document.getOrganization().isDeleted())
                    return null;

            } else {
                if ((document.isInternal() && document.getOrganization() != user.getOrganization()) ||
                        document.getVerifiedStatus() == 0 ||
                        document.getVerifiedStatus() == -1 ||
                        document.getCategory().isDeleted() ||
                        document.getField().isDeleted() ||
                        document.getOrganization().isDeleted())
                    return null;

            }
        } else if (user.getRole().getRoleName().equals("ROLE_MANAGER")) {
            if (user.getOrganization() != document.getUserUploaded().getOrganization())
                return null;
        }

        // Increase views
        if (document.getVerifiedStatus() == 1) {
            document.setTotalView(document.getTotalView() + 1);
            documentRepository.save(document);
        }

        recencyService.addToRecentDocuments(slug);

        DetailDocumentResponseModel documentResponseModel = convertToDetailDocumentModel(document);

        return documentResponseModel;
    }

    @Override
    public DetailDocumentResponseModel viewDocumentForGuest(String slug) {
        Document document = documentRepository.findBySlug(slug).orElse(null);

        if (document == null)
            return null;

        if (document.isInternal() ||
                document.getVerifiedStatus() == 0 ||
                document.getVerifiedStatus() == -1 ||
                document.getCategory().isDeleted() ||
                document.getField().isDeleted() ||
                document.getOrganization().isDeleted())
            return null;

        // Increase views
        document.setTotalView(document.getTotalView() + 1);
        documentRepository.save(document);

        DetailDocumentResponseModel documentResponseModel = convertToDetailDocumentModelForGuest(document);

        return documentResponseModel;
    }

    @Override
    public Page<DocumentResponseModel> getAllDocuments(int page, int size, String order, String category, String field, String organization, String deleted, String internal, String status, String s) {
        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = findCategoryBySlug(category);

        Field foundField = findFieldBySlug(field);

        Organization foundOrganization = findOrganizationBySlug(organization);

        Boolean isDeleted = parseDeleted(deleted);

        Boolean isInternal = parseInternal(internal);

        Integer verifiedStatus = parseStatus(status);

        Page<Document> documents = documentRepository.searchWithAllDocuments(isDeleted, isInternal, verifiedStatus, foundCategory, foundField, foundOrganization, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }
//
//    public Page<DocumentResponseModel> getAllDocumentsByOrganization(String organization, int page, int size, String order, String category, String field, String deleted, String internal, String status, String s) {
//
//        Sort sort = Sort.by(Sort.Direction.DESC, order);
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Organization foundOrganization = organizationRepository.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));
//
//        Category foundCategory = category.equals("all") ?
//                null : categoryRepository.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));
//
//        Field foundField = field.equals("all") ?
//                null : fieldRepository.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));
//
//        Boolean isDeleted = deleted.equals("all") ?
//                null : Boolean.valueOf(deleted);
//
//        Boolean isInternal = internal.equals("all") ?
//                null : Boolean.valueOf(internal);
//
//        Integer verifiedStatus = status.equals("all") ?
//                null : Integer.valueOf(status);
//
//        Page<Document> documents = documentRepository.searchWithAllDocuments(isDeleted, isInternal, verifiedStatus, foundCategory, foundField, foundOrganization, s, pageable);
//        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
//
//        return documentModels;
//    }

    @Override
    public Page<DocumentResponseModel> getMyUploads(int page, int size, String order, String category, String organization, String field, String status, String s) {

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Find user info
        User user = userService.findLoggedInUser();

        Category foundCategory = findCategoryBySlug(category);
        Field foundField = findFieldBySlug(field);
        Organization foundOrganization = findOrganizationBySlug(organization);
        Integer verifiedStatus = parseStatus(status);

        Page<Document> documents = documentRepository.findUploadedDocuments(verifiedStatus, foundCategory, foundField, foundOrganization, user, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> getOwnedDocuments(int page, int size) {
        // Find user info
        User user = userService.findLoggedInUser();

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Document> documents = documentRepository.findByUserUploaded(user, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> getDocumentsByUser(UUID userId, int page, int size) {
        User user = userRepositoty.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Document> documents = documentRepository.findByUserUploaded(user, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> findDocumentsByUserForStudent(UUID userId, int page, int size, String order, String category, String field, String s) {
        User user = userService.findLoggedInUser();

        User userUploaded = userRepositoty.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = findCategoryBySlug(category);

        Field foundField = findFieldBySlug(field);

        Page<Document> documents = documentRepository.findUploadedDocumentsByUserForStudent(foundCategory, foundField, userUploaded.getOrganization(), userUploaded, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> findDocumentsByUserForGuest(UUID userId, int page, int size, String order, String category, String field, String s) {
        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        User userUploaded = userRepositoty.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Category foundCategory = findCategoryBySlug(category);

        Field foundField = findFieldBySlug(field);

        Page<Document> documents = documentRepository.findUploadedDocumentsByUserForGuest(foundCategory, foundField, userUploaded, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> getDocumentsForGuests(int page, int size, String order, String sortOrder, String category, String field, String organization, String s) {
        // Order maybe one of these: totalView, updatedAt, averageRating, totalFavorite
        // Sort order maybe one of these: asc, desc
        Sort sort;
        if (order.equals("totalFavorite")) {
            sort = JpaSort.unsafe(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "SIZE(d.documentLikes)");
        } else {
            sort = Sort.by(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order);
        }

        Category foundCategory = findCategoryBySlug(category);
        Field foundField = findFieldBySlug(field);
        Organization foundOrganization = findOrganizationBySlug(organization);

        Pageable pageable;
        Page<Document> documents = Page.empty();

        if (order.equals("averageRating")) {
            pageable = PageRequest.of(page, size);

            if (s.equals("")) {
                documents = documentRepository.findDocumentsForGuestsOrderByAverageRating(foundCategory, foundField, foundOrganization, pageable);
            } else {
                documents = documentRepository.searchDocumentsForGuestsOrderByAverageRating(foundCategory, foundField, foundOrganization, s, pageable);
            }
        } else {
            pageable = PageRequest.of(page, size, sort);

            if (s.equals("")) {
                documents = documentRepository.findDocumentsForGuests(foundCategory, foundField, foundOrganization, pageable);
            } else {
                documents = documentRepository.searchDocumentsForGuests(foundCategory, foundField, foundOrganization, s, pageable);
            }
        }

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> getPendingDocuments(int page, int size, String status, String organization) {
        User user = userService.findLoggedInUser();

        Integer verifiedStatus = parseStatus(status);

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Document> documents;
        // By organization
        if (user.getRole().getRoleName().equals("ROLE_MANAGER")) {
            Organization userOrganization = user.getOrganization();
            documents = documentRepository.findPendingDocuments(userOrganization, verifiedStatus, pageable);
        } else { // Not include organization
            Organization foundOrganization = findOrganizationBySlug(organization);
            documents = documentRepository.findPendingDocuments(foundOrganization, verifiedStatus, pageable);
        }
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> getDocumentsForStudent(int page, int size, String order, String sortOrder, String category, String field, String organization, String s) {
        // Find user info and organization
        User user = userService.findLoggedInUser();
        Organization userOrganization = user.getOrganization();

        // Order maybe one of these: totalView, updatedAt, averageRating, totalFavorite
        // Sort order maybe one of these: asc, desc
        Sort sort;
        if (order.equals("totalFavorite")) {
            sort = JpaSort.unsafe(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "SIZE(d.documentLikes)");
        } else {
            sort = Sort.by(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order);
        }

        Category foundCategory = findCategoryBySlug(category);
        Field foundField = findFieldBySlug(field);
        Organization foundOrganization = findOrganizationBySlug(organization);

        Pageable pageable;
        Page<Document> documents = Page.empty();

        if (order.equals("averageRating")) {
            pageable = PageRequest.of(page, size);

            if (s.equals("")) {
                documents = documentRepository.findDocumentsForStudentsOrderByAverageRating(foundCategory, foundField, foundOrganization, userOrganization, pageable);
            } else {
                documents = documentRepository.searchDocumentsForStudentsOrderByAverageRating(foundCategory, foundField, foundOrganization, userOrganization, s, pageable);
            }
        } else {
            pageable = PageRequest.of(page, size, sort);

            if (s.equals("")) {
                documents = documentRepository.findDocumentsForStudents(foundCategory, foundField, foundOrganization, userOrganization, pageable);
            } else {
                documents = documentRepository.searchDocumentsForStudents(foundCategory, foundField, foundOrganization, userOrganization, s, pageable);
            }
        }

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public DocumentResponseModel uploadDocument(DocumentRequestModel documentRequestModel,
                                                MultipartFile multipartFile) {
        User user = userService.findLoggedInUser();

        Document document = modelMapper.map(documentRequestModel, Document.class);

        // Upload file document
        FileModel gd = googleDriveService.uploadFile(multipartFile, documentRequestModel.getDocName(), null, null);

        // Find category and field
        Category category = categoryRepository.findById(documentRequestModel.getCategoryId()).orElse(null);
        Field field = fieldRepository.findById(documentRequestModel.getFieldId()).orElse(null);
        Organization organization = organizationRepository.findById(documentRequestModel.getOrgId()).orElse(null);

        // Update file properties for document without overwriting existing properties
        modelMapper.map(gd, document);
        document.setSlug(slugGenerator.generateSlug(document.getDocName().replace(".pdf", ""), true));
        document.setUserUploaded(user);
        document.setCategory(category);
        document.setField(field);
        document.setOrganization(organization);

        // Student must wait for the approval
        if (user.getRole().getRoleName().equals("ROLE_STUDENT")) {
            document.setContributed(true);
            document.setVerifiedStatus(0);
        } else {
            document.setVerifiedStatus(1);
            document.setUserVerified(user);
        }

        addTags(document);

        documentRepository.save(document);

        DocumentResponseModel savedDocument = modelMapper.map(document, DocumentResponseModel.class);

        return savedDocument;
    }

    @Override
    public DocumentResponseModel updateDocument(String slug, DocumentRequestModel documentRequestModel,
                                                MultipartFile multipartFile) {
        User user = userService.findLoggedInUser();

        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found!"));

        boolean nameAndIntroductionNotChanged = document.getDocName().equals(documentRequestModel.getDocName())
                && document.getDocIntroduction().equals(documentRequestModel.getDocIntroduction());

        if (!document.getDocName().equals(documentRequestModel.getDocName()))
            document.setSlug(slugGenerator.generateSlug(document.getDocName().replace(".pdf", ""), true));
        document.setDocName(documentRequestModel.getDocName());
        document.setDocIntroduction(documentRequestModel.getDocIntroduction());
        document.setInternal(documentRequestModel.isInternal());

        if (multipartFile != null) {
            // Get id of old file and thumbnail file
            String fileId = document.getFileId() != null ? document.getFileId() : null;
            String thumbnailId = document.getThumbnail() != null ? stringHandler.getFileId(document.getThumbnail()) : null;
            // Upload file
            FileModel gd = googleDriveService.uploadFile(multipartFile, documentRequestModel.getDocName(), fileId, thumbnailId);
            // Update file properties for document without overwriting existing properties
            document.setThumbnail(gd.getThumbnail());
            document.setViewUrl(gd.getViewUrl());
            document.setDownloadUrl(gd.getDownloadUrl());
            document.setFileId(gd.getFileId());
            document.setTotalPages(gd.getTotalPages());
        }

        // Find category, field and organization
        Category category = categoryRepository.findById(documentRequestModel.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        Field field = fieldRepository.findById(documentRequestModel.getFieldId()).orElseThrow(() -> new RuntimeException("Field not found"));
        Organization organization = organizationRepository.findById(documentRequestModel.getOrgId()).orElseThrow(() -> new RuntimeException("Organization not found"));
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

        if (!nameAndIntroductionNotChanged) {
            document.getTags().clear();
            addTags(document);
        }

        document = documentRepository.save(document);

        DocumentResponseModel savedDocument = modelMapper.map(document, DocumentResponseModel.class);

        return savedDocument;
    }

    @Override
    public void deleteDocument(UUID docId) {
        Document document = documentRepository.findById(docId).orElseThrow(() -> new RuntimeException("Document not found!"));
        googleDriveService.deleteFile(stringHandler.getFileId(document.getThumbnail()));
        googleDriveService.deleteFile(document.getFileId());
        documentRepository.delete(document);
    }

    @Override
    public void approveDocument(UUID docId, boolean isApproved, String note) {
        // Find user info
        User user = userService.findLoggedInUser();
        // Find document
        Document document = documentRepository.findById(docId).orElseThrow(() -> new RuntimeException("Document not found!"));

        if (isApproved) {
            document.setVerifiedStatus(1);
            document.setNote(null);
            notificationService.sendNotification(NotificationMessage.ACCEPT_DOCUMENT.name(), NotificationMessage.ACCEPT_DOCUMENT.getMessage(), user, document.getUserUploaded(), document);
        } else {
            document.setVerifiedStatus(-1);
            document.setNote(note);
            String reason = note.equals("") ? "" : " vì " + note;
            notificationService.sendNotification(NotificationMessage.REJECT_DOCUMENT.name(), NotificationMessage.REJECT_DOCUMENT.getMessage() + reason, user, document.getUserUploaded(), document);
        }
        document.setUserVerified(user);
        document.setVerifiedAt(new Timestamp(System.currentTimeMillis()));

        documentRepository.save(document);
    }

    @Override
    public Page<DocumentResponseModel> getLatestDocuments(int page, int size, String order, String category, String field, String organization, String deleted, String internal, String status, String s) {
        Sort sort = Sort.by(Sort.Direction.DESC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Organization foundOrganization = findOrganizationBySlug(organization);
        Category foundCategory = findCategoryBySlug(category);
        Field foundField = findFieldBySlug(field);
        Boolean isDeleted = parseDeleted(deleted);
        Boolean isInternal = parseInternal(internal);
        Integer verifiedStatus = parseStatus(status);

        Page<Document> documents = documentRepository.searchLatestDocuments(isDeleted, isInternal, verifiedStatus, foundCategory, foundField, foundOrganization, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> findRelatedDocuments(String slug) {
        User user = userService.findLoggedInUser();
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Document> documents = documentRepository.findRelatedDocumentsByTags(document, user.getOrganization(), pageable);
        Page<DocumentResponseModel> documentResponseModels = documents.map(this::convertToDocumentModel);

        return documentResponseModels;
    }

    @Override
    public Page<DocumentResponseModel> findRelatedDocumentsForGuest(String slug) {
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Document> documents = documentRepository.findRelatedDocumentsByTagsForGuest(document, pageable);
        Page<DocumentResponseModel> documentResponseModels = documents.map(this::convertToDocumentModel);

        return documentResponseModels;
    }

    public Organization findOrganizationBySlug(String organizationSlug) {
        return organizationSlug.equals("all") ?
                null : organizationRepository.findBySlug(organizationSlug).orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    public Category findCategoryBySlug(String categorySlug) {
        return categorySlug.equals("all") ?
                null : categoryRepository.findBySlug(categorySlug)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Field findFieldBySlug(String fieldSlug) {
        return fieldSlug.equals("all") ?
                null : fieldRepository.findBySlug(fieldSlug)
                .orElseThrow(() -> new RuntimeException("Field not found"));
    }

    public Boolean parseDeleted(String deleted) {
        return deleted.equals("all") ? null : Boolean.valueOf(deleted);
    }

    public Boolean parseInternal(String internal) {
        return internal.equals("all") ? null : Boolean.valueOf(internal);
    }

    public Integer parseStatus(String status) {
        return status.equals("all") ? null : Integer.valueOf(status);
    }

    public Boolean parseContributed(String contributed) {
        return contributed.equals("all") ? null : Boolean.valueOf(contributed);
    }

    public void addTags(Document document) {
        try {
            List<String> keywords = tagExtractor.findKeywords(document.getDocName().concat(". ").concat(document.getDocIntroduction()));
            for (String keyword : keywords) {
                boolean isExisted = tagRepository.existsByTagName(keyword);
                Tag tag = new Tag();
                if (isExisted) {
                    tag = tagRepository.findByTagName(keyword).orElseThrow(() -> new RuntimeException("Tag not found"));
                } else {
                    tag.setTagName(keyword);
                    tag.setSlug(SlugGenerator.generateSlug(keyword, false));
                    tag = tagRepository.save(tag);
                }

                document.getTags().add(tag);
                documentRepository.save(document);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DocumentResponseModel convertToDocumentModel(Document document) {
        DocumentResponseModel documentResponseModel = modelMapper.map(document, DocumentResponseModel.class);

        int totalLikes = document.getDocumentLikes().size();
        int totalReviews = (int) document.getReviews().stream().filter(review -> review.getVerifiedStatus() == 1).count();
        int totalRating = document.getReviews()
                .stream()
                .filter(review -> review.getVerifiedStatus() == 1)
                .mapToInt(Review::getStar)
                .sum();
        double averageRating = (double) totalRating / totalReviews;

        documentResponseModel.setTotalFavorite(totalLikes);
        documentResponseModel.setAverageRating(averageRating);

        return documentResponseModel;
    }

    private DetailDocumentResponseModel convertToDetailDocumentModel(Document document) {
        User user = userService.findLoggedInUser();
        DetailDocumentResponseModel documentResponseModel = modelMapper.map(document, DetailDocumentResponseModel.class);

        int totalLikes = document.getDocumentLikes().size();
        int totalReviews = (int) document.getReviews()
                .stream()
                .filter(review -> review.getVerifiedStatus() == 1)
                .count();
        int totalRating = document.getReviews()
                .stream()
                .filter(review -> review.getVerifiedStatus() == 1)
                .mapToInt(Review::getStar)
                .sum();
        double averageRating = totalReviews == 0 ? 0 : (double) totalRating / totalReviews;
        boolean isLiked = documentLikeRepository.existsByUserAndDocument(user, document);
        boolean isSaved = saveRepository.existsByUserAndDocument(user, document);
        boolean isReviewed = reviewRepository.existsByUserAndDocument(user, document);
        Optional<Recency> recency = recencyRepository.findByUserAndDocument(user, document);

        documentResponseModel.setTotalFavorite(totalLikes);
        documentResponseModel.setTotalReviews(totalReviews);
        documentResponseModel.setAverageRating(averageRating);
        documentResponseModel.setLiked(isLiked);
        documentResponseModel.setSaved(isSaved);
        documentResponseModel.setReviewed(isReviewed);
        if (recency.isPresent()) {
            documentResponseModel.setCurrentPage(recency.get().getCurrentPage());
        } else {
            documentResponseModel.setCurrentPage(0);
        }

        return documentResponseModel;
    }

    private DetailDocumentResponseModel convertToDetailDocumentModelForGuest(Document document) {
        DetailDocumentResponseModel documentResponseModel = modelMapper.map(document, DetailDocumentResponseModel.class);

        int totalLikes = document.getDocumentLikes().size();
        int totalReviews = (int) document.getReviews()
                .stream()
                .filter(review -> review.getVerifiedStatus() == 1)
                .count();
        int totalRating = document.getReviews()
                .stream()
                .filter(review -> review.getVerifiedStatus() == 1)
                .mapToInt(Review::getStar)
                .sum();
        double averageRating = totalReviews == 0 ? 0 : (double) totalRating / totalReviews;

        documentResponseModel.setTotalFavorite(totalLikes);
        documentResponseModel.setTotalReviews(totalReviews);
        documentResponseModel.setAverageRating(averageRating);

        return documentResponseModel;
    }

}
