package com.major_project.digital_library.service.impl;

import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.request_model.DocumentRequestModel;
import com.major_project.digital_library.model.response_model.DetailDocumentResponseModel;
import com.major_project.digital_library.model.response_model.DocumentResponseModel;
import com.major_project.digital_library.repository.*;
import com.major_project.digital_library.service.ICollectionDocumentService;
import com.major_project.digital_library.service.IDocumentService;
import com.major_project.digital_library.service.IUserService;
import com.major_project.digital_library.util.GoogleDriveUpload;
import com.major_project.digital_library.util.SlugGenerator;
import com.major_project.digital_library.util.StringHandler;
import com.major_project.digital_library.yake.TagExtractor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements IDocumentService {
    private final IDocumentRepository documentRepository;
    private final ICategoryRepository categoryRepository;
    private final IFieldRepository fieldRepository;
    private final IOrganizationRepository organizationRepository;
    private final IUserRepositoty userRepositoty;
    private final IDocumentLikeRepository documentLikeRepository;
    private final ISaveRepository saveRepository;
    private final IReviewRepository reviewRepository;
    private final ICollectionRepository collectionRepository;
    private final ICollectionDocumentService collectionDocumentService;
    private final IUserService userService;
    private final ModelMapper modelMapper;
    private final SlugGenerator slugGenerator;
    private final TagExtractor tagExtractor;

    private final GoogleDriveUpload googleDriveUpload;
    private final StringHandler stringHandler;

    @Autowired
    public DocumentServiceImpl(IDocumentRepository documentRepository, ICategoryRepository categoryRepository, IFieldRepository fieldRepository, IOrganizationRepository organizationRepository, IUserRepositoty userRepositoty, IDocumentLikeRepository documentLikeRepository, ISaveRepository saveRepository, IReviewRepository reviewRepository, ICollectionRepository collectionRepository, ICollectionDocumentService collectionDocumentService, IUserService userService, ModelMapper modelMapper, SlugGenerator slugGenerator, TagExtractor tagExtractor, GoogleDriveUpload googleDriveUpload, StringHandler stringHandler) {
        this.documentRepository = documentRepository;
        this.categoryRepository = categoryRepository;
        this.fieldRepository = fieldRepository;
        this.organizationRepository = organizationRepository;
        this.userRepositoty = userRepositoty;
        this.documentLikeRepository = documentLikeRepository;
        this.saveRepository = saveRepository;
        this.reviewRepository = reviewRepository;
        this.collectionRepository = collectionRepository;
        this.collectionDocumentService = collectionDocumentService;
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.slugGenerator = slugGenerator;
        this.tagExtractor = tagExtractor;
        this.googleDriveUpload = googleDriveUpload;
        this.stringHandler = stringHandler;
    }

    @Override
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    @Override
    public <S extends Document> S save(S entity) {
        return documentRepository.save(entity);
    }

    @Override
    public <S extends Document> List<S> saveAll(Iterable<S> entities) {
        return documentRepository.saveAll(entities);
    }

    @Override
    public Optional<Document> findById(UUID uuid) {
        return documentRepository.findById(uuid);
    }

    @Override
    public void deleteById(UUID uuid) {
        documentRepository.deleteById(uuid);
    }

    @Override
    public Page<Document> findAll(Pageable pageable) {
        return documentRepository.findAll(pageable);
    }

    @Override
    public Optional<Document> findBySlug(String slug) {
        return documentRepository.findBySlug(slug);
    }

    @Override
    public Page<Document> findByDocNameContaining(String docName, Pageable pageable) {
        return documentRepository.findByDocNameContaining(docName, pageable);
    }

    @Override
    public Page<Document> findByDocNameAndOrganizationContaining(String docName, Organization organization, Pageable pageable) {
        return documentRepository.findByDocNameAndOrganizationContaining(docName, organization, pageable);
    }

    @Override
    public Page<Document> findAllByOrganization(Organization organization, Pageable pageable) {
        return documentRepository.findAllByOrganization(organization, pageable);
    }

    @Override
    public Page<Document> findByUserUploaded(User user, Pageable pageable) {
        return documentRepository.findByUserUploaded(user, pageable);
    }

    @Override
    public Page<Document> findByUserUploadedAndIsDeleted(User user, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByUserUploadedAndIsDeleted(user, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByVerifiedStatusAndIsDeleted(int VerifiedStatus, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByVerifiedStatusAndIsDeleted(VerifiedStatus, isDeleted, pageable);
    }

    @Override
    public Page<Document> findByOrganizationAndVerifiedStatusAndIsDeleted(Organization organization, int verifiedStatus, boolean isDeleted, Pageable pageable) {
        return documentRepository.findByOrganizationAndVerifiedStatusAndIsDeleted(organization, verifiedStatus, isDeleted, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND d.userUploaded = ?1 " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = ?2))")
    public Page<Document> findByUserUploaded(User user, Organization userOrganization, Pageable pageable) {
        return documentRepository.findByUserUploaded(user, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL)")
    public Page<Document> findAllDocumentsWithFilter(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, Pageable pageable) {
        return documentRepository.findAllDocumentsWithFilter(isDeleted, isInternal, verifiedStatus, category, field, organization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = :userOrganization))")
    public Page<Document> findDocumentsForStudents(Category category, Field field, Organization organization, Organization userOrganization, Pageable pageable) {
        return documentRepository.findDocumentsForStudents(category, field, organization, userOrganization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false)")
    public Page<Document> findDocumentsForGuests(Category category, Field field, Organization organization, Pageable pageable) {
        return documentRepository.findDocumentsForGuests(category, field, organization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = :userOrganization)) " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    public Page<Document> searchDocumentsForStudents(Category category, Field field, Organization organization, Organization userOrganization, String query, Pageable pageable) {
        return documentRepository.searchDocumentsForStudents(category, field, organization, userOrganization, query, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND d.verifiedStatus = 1 " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (d.isInternal = false) " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    public Page<Document> searchDocumentsForGuests(Category category, Field field, Organization organization, String query, Pageable pageable) {
        return documentRepository.searchDocumentsForGuests(category, field, organization, query, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    public Page<Document> searchWithAllDocuments(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, String query, Pageable pageable) {
        return documentRepository.searchWithAllDocuments(isDeleted, isInternal, verifiedStatus, category, field, organization, query, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%'))) " +
            "AND MONTH(d.uploadedAt) = MONTH(CURRENT_DATE) AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE)")
    public Page<Document> searchLatestDocuments(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, String query, Pageable pageable) {
        return documentRepository.searchLatestDocuments(isDeleted, isInternal, verifiedStatus, category, field, organization, query, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d " +
            "WHERE (d.isDeleted = :isDeleted OR :isDeleted IS NULL) " +
            "AND (d.isInternal = :isInternal OR :isInternal IS NULL) " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND MONTH(d.uploadedAt) = MONTH(CURRENT_DATE) AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE)")
    public Page<Document> findLatestDocuments(Boolean isDeleted, Boolean isInternal, Integer verifiedStatus, Category category, Field field, Organization organization, Pageable pageable) {
        return documentRepository.findLatestDocuments(isDeleted, isInternal, verifiedStatus, category, field, organization, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND (d.verifiedStatus = :verifiedStatus OR :verifiedStatus IS NULL) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND (d.organization = :organization OR :organization IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND d.userUploaded = :userUploaded " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    public Page<Document> findUploadedDocuments(Integer verifiedStatus, Category category, Field field, Organization organization, User userUploaded, String query, Pageable pageable) {
        return documentRepository.findUploadedDocuments(verifiedStatus, category, field, organization, userUploaded, query, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND (d.verifiedStatus = 1) " +
            "AND (d.isInternal = false OR (d.isInternal = true AND d.organization = :userOrganization)) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND d.userUploaded = :userUploaded " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    public Page<Document> findUploadedDocumentsByUserForStudent(Category category, Field field, Organization userOrganization, User userUploaded, String query, Pageable pageable) {
        return documentRepository.findUploadedDocumentsByUserForStudent(category, field, userOrganization, userUploaded, query, pageable);
    }

    @Override
    @Query("SELECT d FROM Document d WHERE d.isDeleted = false " +
            "AND (d.verifiedStatus = 1) " +
            "AND (d.isInternal = false) " +
            "AND (d.category = :category OR :category IS NULL) " +
            "AND (d.field = :field OR :field IS NULL) " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND d.userUploaded = :userUploaded " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    public Page<Document> findUploadedDocumentsByUserForGuest(Category category, Field field, User userUploaded, String query, Pageable pageable) {
        return documentRepository.findUploadedDocumentsByUserForGuest(category, field, userUploaded, query, pageable);
    }

    @Override
    @Query("SELECT s.document FROM Save s " +
            "JOIN s.document d " +
            "WHERE s.user = :user " +
            "AND s.isSaved = true " +
            "AND (d.isInternal = false OR d.organization = s.user.organization) " +
            "AND d.isDeleted = false " +
            "AND d.category.isDeleted = false " +
            "AND d.field.isDeleted = false " +
            "AND d.organization.isDeleted = false " +
            "AND (LOWER(d.docName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.docIntroduction) LIKE LOWER(CONCAT('%', :query, '%')))")
    public Page<Document> findLikedDocuments(User user, String query, Pageable pageable) {
        return documentRepository.findLikedDocuments(user, query, pageable);
    }

    @Override
    public long count() {
        return documentRepository.count();
    }

    @Override
    public long countByVerifiedStatus(int verifiedStatus) {
        return documentRepository.countByVerifiedStatus(verifiedStatus);
    }

    @Override
    public long countByOrganization(Organization organization) {
        return documentRepository.countByOrganization(organization);
    }


    @Override
    public long countByVerifiedStatusAndOrganization(int verifiedStatus, Organization organization) {
        return documentRepository.countByVerifiedStatusAndOrganization(verifiedStatus, organization);
    }

    @Override
    @Query("SELECT MONTH(d.uploadedAt) as month, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "AND YEAR(d.uploadedAt) = YEAR(CURRENT_DATE) " +
            "GROUP BY MONTH(d.uploadedAt)")
    public List<Object[]> countDocumentsByMonth(Organization organization) {
        return documentRepository.countDocumentsByMonth(organization);
    }

    @Override
    @Query("SELECT d.category.categoryName as category, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "GROUP BY d.category")
    public List<Object[]> countDocumentsByCategory(Organization organization) {
        return documentRepository.countDocumentsByCategory(organization);
    }

    @Override
    @Query("SELECT d.field.fieldName as category, COUNT(d) as count " +
            "FROM Document d " +
            "WHERE (d.organization = :organization OR :organization IS NULL) " +
            "GROUP BY d.field")
    public List<Object[]> countDocumentsByField(Organization organization) {
        return documentRepository.countDocumentsByField(organization);
    }

    @Override
    @Query("SELECT d.organization.orgName as organization, COUNT(d) as count " +
            "FROM Document d " +
            "GROUP BY d.organization")
    public List<Object[]> countDocumentsByOrganization() {
        return documentRepository.countDocumentsByOrganization();
    }

    @Override
    public DetailDocumentResponseModel viewDocument(String slug) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));
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
        document.setTotalView(document.getTotalView() + 1);
        documentRepository.save(document);

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
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not logged in"));

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
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Document> documents = documentRepository.findByUserUploaded(user, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> getDocumentsByUser(UUID userId, int page, int size) {
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Document> documents = documentRepository.findByUserUploaded(user, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> findDocumentsByUserForStudent(UUID userId, int page, int size, String order, String category, String field, String s) {
        User user = userService.findLoggedInUser().orElse(null);

        User userUploaded = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

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

        User userUploaded = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

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
        Sort sort = Sort.by(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = findCategoryBySlug(category);
        Field foundField = findFieldBySlug(field);
        Organization foundOrganization = findOrganizationBySlug(organization);

        Page<Document> documents = Page.empty();
        if (s.equals(""))
            documents = documentRepository.findDocumentsForGuests(foundCategory, foundField, foundOrganization, pageable);
        else
            documents = documentRepository.searchDocumentsForGuests(foundCategory, foundField, foundOrganization, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

//    public Page<DocumentResponseModel> searchDocumentsForGuests(int page, int size, String order, String sortOrder, String category, String field, String organization, String s) {
//        // Order maybe one of these: totalView, updatedAt, averageRating, totalFavorite
//        // Sort order maybe one of these: asc, desc
//        Sort sort = Sort.by(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order);
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Category foundCategory = findCategoryBySlug(category);
//        Field foundField = findFieldBySlug(field);
//        Organization foundOrganization = findOrganizationBySlug(organization);
//
//        Page<Document> documents = documentRepository.searchDocumentsForGuests(foundCategory, foundField, foundOrganization, s, pageable);
//
//        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
//
//        return documentModels;
//    }

    @Override
    public Page<DocumentResponseModel> getPendingDocuments(int page, int size) {
        // Order maybe one of these: docId, totalView
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt");
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Document> documents;

        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        // By organization
        if (user.getRole().getRoleName().equals("ROLE_MANAGER")) {
            Organization foundOrganization = user.getOrganization();
            documents = documentRepository.findByOrganizationAndVerifiedStatusAndIsDeleted(foundOrganization, 0, false, pageable);
        } else { // Not include organization
            documents = documentRepository.findByVerifiedStatusAndIsDeleted(0, false, pageable);
        }
        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

    @Override
    public Page<DocumentResponseModel> getDocumentsForStudent(int page, int size, String order, String sortOrder, String category, String field, String organization, String s) {
        // Find user info and organization
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));
        Organization userOrganization = user.getOrganization();

        // Order maybe one of these: totalView, updatedAt, averageRating, totalFavorite
        // Sort order maybe one of these: asc, desc
        Sort sort = Sort.by(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order);
        Pageable pageable = PageRequest.of(page, size, sort);

        Category foundCategory = findCategoryBySlug(category);
        Field foundField = findFieldBySlug(field);
        Organization foundOrganization = findOrganizationBySlug(organization);

        Page<Document> documents = Page.empty();
        if (s.equals(""))
            documents = documentRepository.findDocumentsForStudents(foundCategory, foundField, foundOrganization, userOrganization, pageable);
        else
            documents = documentRepository.searchDocumentsForStudents(foundCategory, foundField, foundOrganization, userOrganization, s, pageable);

        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);

        return documentModels;
    }

//    public ResponseEntity<?> searchDocumentsForStudents(int page, int size, String order, String sortOrder, String category, String field, String organization, String s) {
//        // Find user info and organization
//        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));
//        Organization userOrganization = user.getOrganization();
//
//        // Order maybe one of these: totalView, updatedAt, averageRating, totalFavorite
//        // Sort order maybe one of these: asc, desc
//        Sort sort = Sort.by(sortOrder.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, order);
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        Category foundCategory = category.equals("all") ?
//                null : categoryRepository.findBySlug(category).orElseThrow(() -> new RuntimeException("Category not found"));
//
//        Field foundField = field.equals("all") ?
//                null : fieldRepository.findBySlug(field).orElseThrow(() -> new RuntimeException("Field not found"));
//
//        Organization foundOrganization = organization.equals("all") ?
//                null : organizationRepository.findBySlug(organization).orElseThrow(() -> new RuntimeException("Organization not found"));
//
//        Page<Document> documents = documentRepository.searchDocumentsForStudents(foundCategory, foundField, foundOrganization, userOrganization, s, pageable);
//
//        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
//        return ResponseEntity.ok(ResponseModel
//                .builder()
//                .status(200)
//                .error(false)
//                .message("Get documents for students successfully")
//                .data(documentModels)
//                .build());
//    }


    @Override
    public DocumentResponseModel uploadDocument(DocumentRequestModel documentRequestModel,
                                                MultipartFile multipartFile) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Document document = modelMapper.map(documentRequestModel, Document.class);

        // Upload file document
        FileModel gd = googleDriveUpload.uploadFile(multipartFile, documentRequestModel.getDocName(), null, null);

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
            document.setVerifiedStatus(0);
        } else {
            document.setVerifiedStatus(1);
            document.setUserVerified(user);
        }

        documentRepository.save(document);

        DocumentResponseModel savedDocument = modelMapper.map(document, DocumentResponseModel.class);

        return savedDocument;
    }

    @Override
    public DocumentResponseModel updateDocument(String slug, DocumentRequestModel documentRequestModel,
                                                MultipartFile multipartFile) {
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found!"));
        if (!document.getDocName().equals(documentRequestModel.getDocName()))
            document.setSlug(slugGenerator.generateSlug(document.getDocName().replace(".pdf", ""), true));
        document.setDocName(documentRequestModel.getDocName());
        document.setDocIntroduction(documentRequestModel.getDocIntroduction());
        document.setInternal(documentRequestModel.isInternal());

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

        document = documentRepository.save(document);

        DocumentResponseModel savedDocument = modelMapper.map(document, DocumentResponseModel.class);

        return savedDocument;
    }

    @Override
    public void deleteDocument(UUID docId) {
        Document document = documentRepository.findById(docId).orElseThrow(() -> new RuntimeException("Document not found!"));
        googleDriveUpload.deleteFile(stringHandler.getFileId(document.getThumbnail()));
        googleDriveUpload.deleteFile(stringHandler.getFileId(document.getViewUrl()));
        documentRepository.delete(document);
    }

    @Override
    public void approveDocument(UUID docId, boolean isApproved, String note) {
        // Find user info
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));
        // Find document
        Document document = documentRepository.findById(docId).orElseThrow(() -> new RuntimeException("Document not found!"));

        if (isApproved) {
            document.setVerifiedStatus(1);
        } else {
            document.setVerifiedStatus(-1);
            document.setNote(note);
        }
        document.setUserVerified(user);

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

//    public Page<DocumentResponseModel> getLatestDocumentsByOrganization(String organization, int page, int size, String order, String category, String field, String deleted, String internal, String status, String s) {
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
//        Page<Document> documents = documentRepository.searchLatestDocuments(isDeleted, isInternal, verifiedStatus, foundCategory, foundField, foundOrganization, s, pageable);
//
//        Page<DocumentResponseModel> documentModels = documents.map(this::convertToDocumentModel);
//
//        return documentModels;
//    }

    @Override
    public Page<DocumentResponseModel> findRelatedDocuments(String slug) {
        Document document = documentRepository.findBySlug(slug).orElseThrow(() -> new RuntimeException("Document not found"));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Document> documents = documentRepository.findRelatedDocumentsByTags(document, document.getTags(), pageable);
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
        User user = userService.findLoggedInUser().orElseThrow(() -> new RuntimeException("User not found"));

        DetailDocumentResponseModel documentResponseModel = modelMapper.map(document, DetailDocumentResponseModel.class);

        int totalLikes = document.getDocumentLikes().size();
        int totalReviews = (int) document.getReviews().stream().filter(review -> review.getVerifiedStatus() == 1).count();
        int totalRating = document.getReviews()
                .stream()
                .filter(review -> review.getVerifiedStatus() == 1)
                .mapToInt(Review::getStar)
                .sum();
        double averageRating = totalReviews == 0 ? 0 : (double) totalRating / totalReviews;

        boolean isLiked = documentLikeRepository.existsByUserAndDocument(user, document);
        boolean isSaved = saveRepository.existsByUserAndDocument(user, document);
        boolean isReviewed = reviewRepository.existsByUserAndDocument(user, document);

        documentResponseModel.setTotalFavorite(totalLikes);
        documentResponseModel.setTotalReviews(totalReviews);
        documentResponseModel.setAverageRating(averageRating);
        documentResponseModel.setLiked(isLiked);
        documentResponseModel.setSaved(isSaved);
        documentResponseModel.setReviewed(isReviewed);

        return documentResponseModel;
    }

    private DetailDocumentResponseModel convertToDetailDocumentModelForGuest(Document document) {
        DetailDocumentResponseModel documentResponseModel = modelMapper.map(document, DetailDocumentResponseModel.class);

        int totalLikes = document.getDocumentLikes().size();
        int totalReviews = document.getReviews().size();
        int totalRating = document.getReviews()
                .stream()
                .mapToInt(Review::getStar)
                .sum();
        double averageRating = totalReviews == 0 ? 0 : (double) totalRating / totalReviews;

        documentResponseModel.setTotalFavorite(totalLikes);
        documentResponseModel.setTotalReviews(totalReviews);
        documentResponseModel.setAverageRating(averageRating);

        return documentResponseModel;
    }

}
