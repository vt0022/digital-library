package com.major_project.digital_library.controller;

import com.major_project.digital_library.constant.BadgeUnit;
import com.major_project.digital_library.constant.NotificationMessage;
import com.major_project.digital_library.entity.Collection;
import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.repository.*;
import com.major_project.digital_library.service.*;
import com.major_project.digital_library.service.other.GoogleDriveService;
import com.major_project.digital_library.util.SlugGenerator;
import com.major_project.digital_library.yake.TagExtractor;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.komamitsu.fastuuidparser.FastUuidParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/v2/init")
public class InitController {
    private final IUserService userService;
    private final IDocumentService documentService;
    private final IFieldService fieldService;
    private final ICategoryService categoryService;
    private final IOrganizationService organizationService;
    private final IRoleService roleService;
    private final ISaveService saveService;
    private final ISaveRepository saveRepository;
    private final IReviewService reviewService;
    private final IDocumentRepository documentRepository;
    private final IDocumentLikeService favoriteService;
    private final IDocumentLikeRepository documentLikeRepository;
    private final IRecencyService recencyService;
    private final IRecencyRepository recencyRepository;
    private final IPostLikeService postLikeService;
    private final IPostLikeRepository postLikeRepository;
    private final IReplyLikeRepository replyLikeRepository;
    private final IReplyRepository replyRepository;
    private final INotificationRepository notificationRepository;
    private final IReplyService replyService;
    private final IPostService postService;
    private final IReviewRepository reviewRepository;
    private final IReplyLikeService replyLikeService;
    private final ISubsectionRepository subsectionRepository;
    private final ISectionRepository sectionRepository;
    private final ILabelRepository labelRepository;
    private final IBadgeTypeRepository badgeTypeRepository;
    private final IBadgeRepository badgeRepository;
    private final GoogleDriveService googleDriveService;
    private final ModelMapper modelMapper;
    private final SlugGenerator slugGenerator;
    private final IUserRepository userRepositoty;
    private final IBadgeRewardRepository badgeRewardRepository;
    private final ITagRepository tagRepository;
    private final IPostRepository postRepository;
    private final ICollectionRepository collectionRepository;
    private final ICollectionDocumentRepository collectionDocumentRepository;
    private final IPostHistoryRepository postHistoryRepository;
    private final IReplyHistoryRepository replyHistoryRepository;
    private final IUserRepository userRepository;
    private final TagExtractor tagExtractor;

    @Autowired
    public InitController(IUserService userService, IDocumentService documentService, IFieldService fieldService, ICategoryService categoryService, IOrganizationService organizationService, IRoleService roleService, ISaveService saveService, ISaveRepository saveRepository, IReviewService reviewService, IDocumentRepository documentRepository, IDocumentLikeService favoriteService, IDocumentLikeRepository documentLikeRepository, IRecencyService recencyService, IRecencyRepository recencyRepository, IPostLikeService postLikeService, IPostLikeRepository postLikeRepository, IReplyLikeRepository replyLikeRepository, IReplyRepository replyRepository, INotificationRepository notificationRepository, IReplyService replyService, IPostService postService, IReviewRepository reviewRepository, IReplyLikeService replyLikeService, ISubsectionRepository subsectionRepository, ISectionRepository sectionRepository, ILabelRepository labelRepository, IBadgeTypeRepository badgeTypeRepository, IBadgeRepository badgeRepository, GoogleDriveService googleDriveService, ModelMapper modelMapper, SlugGenerator slugGenerator, IUserRepository userRepositoty, IBadgeRewardRepository badgeRewardRepository, ITagRepository tagRepository, IPostRepository postRepository, ICollectionRepository collectionRepository, ICollectionDocumentRepository collectionDocumentRepository, IPostHistoryRepository postHistoryRepository, IReplyHistoryRepository replyHistoryRepository, IUserRepository userRepository, TagExtractor tagExtractor) {
        this.userService = userService;
        this.documentService = documentService;
        this.fieldService = fieldService;
        this.categoryService = categoryService;
        this.organizationService = organizationService;
        this.roleService = roleService;
        this.saveService = saveService;
        this.saveRepository = saveRepository;
        this.reviewService = reviewService;
        this.documentRepository = documentRepository;
        this.favoriteService = favoriteService;
        this.documentLikeRepository = documentLikeRepository;
        this.recencyService = recencyService;
        this.recencyRepository = recencyRepository;
        this.postLikeService = postLikeService;
        this.postLikeRepository = postLikeRepository;
        this.replyLikeRepository = replyLikeRepository;
        this.replyRepository = replyRepository;
        this.notificationRepository = notificationRepository;
        this.replyService = replyService;
        this.postService = postService;
        this.reviewRepository = reviewRepository;
        this.replyLikeService = replyLikeService;
        this.subsectionRepository = subsectionRepository;
        this.sectionRepository = sectionRepository;
        this.labelRepository = labelRepository;
        this.badgeTypeRepository = badgeTypeRepository;
        this.badgeRepository = badgeRepository;
        this.googleDriveService = googleDriveService;
        this.modelMapper = modelMapper;
        this.slugGenerator = slugGenerator;
        this.userRepositoty = userRepositoty;
        this.badgeRewardRepository = badgeRewardRepository;
        this.tagRepository = tagRepository;
        this.postRepository = postRepository;
        this.collectionRepository = collectionRepository;
        this.collectionDocumentRepository = collectionDocumentRepository;
        this.postHistoryRepository = postHistoryRepository;
        this.replyHistoryRepository = replyHistoryRepository;
        this.userRepository = userRepository;
        this.tagExtractor = tagExtractor;
    }

    @PostMapping("/documents")
    public ResponseEntity<?> initDocuments() throws IOException {
        FileInputStream file = new FileInputStream("D:/Nam4/Ky1/TieuLuanChuyenNganh/DocInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Cell pathCell = row.getCell(0);
            Cell introCell = row.getCell(1);
            Cell categoryCell = row.getCell(2);
            Cell fieldCell = row.getCell(3);
            Cell orgCell = row.getCell(4);
            Cell uploadCell = row.getCell(5);
            Cell verifyCell = row.getCell(6);

            DataFormatter formatter = new DataFormatter();

            if (pathCell != null) {
                String path = formatter.formatCellValue(pathCell);
                String introduction = formatter.formatCellValue(introCell);
                UUID categoryId = UUID.fromString(formatter.formatCellValue(categoryCell));
                UUID fieldId = UUID.fromString(formatter.formatCellValue(fieldCell));
                UUID orgId = UUID.fromString(formatter.formatCellValue(orgCell));
                UUID uploadedBy = UUID.fromString(formatter.formatCellValue(uploadCell));
                UUID verifiedBy = UUID.fromString(formatter.formatCellValue(verifyCell));

                File docFile = new File(path);
                FileInputStream input = new FileInputStream(docFile);
                MultipartFile multipartFile = new MockMultipartFile(docFile.getName(),
                        docFile.getName(), "application/pdf", IOUtils.toByteArray(input));
                try {
                    // Upload file document
                    FileModel gd = googleDriveService.uploadFile(multipartFile, docFile.getName(), null, null);

                    // Find
                    Category category = categoryService.findById(categoryId).orElse(null);
                    Field field = fieldService.findById(fieldId).orElse(null);
                    Organization organization = organizationService.findById(orgId).orElse(null);
                    User userUploaded = userRepository.findById(uploadedBy).orElse(null);
                    User userVerified = userRepository.findById(verifiedBy).orElse(null);

                    // Map
                    Document document = modelMapper.map(gd, Document.class);

                    document.setDocName(docFile.getName().replace(".pdf", ""));
                    document.setDocIntroduction(introduction);
                    document.setSlug(slugGenerator.generateSlug(document.getDocName().replace(".pdf", ""), true));

                    document.setUserUploaded(userUploaded);
                    document.setUserVerified(userVerified);
                    document.setCategory(category);
                    document.setField(field);
                    document.setOrganization(organization);

                    Random random = new Random();
                    int randomNumber = random.nextInt(191) + 10;
                    document.setTotalView(randomNumber);

                    documentRepository.save(document);
                } catch (Exception e) {
                    System.out.println(docFile.getName());
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Upload documents successfully")
                .build());
    }

    @PostMapping("/users")
    public ResponseEntity<?> initUsers() throws IOException {
        FileInputStream file = new FileInputStream("D:/Nam4/Ky1/TieuLuanChuyenNganh/UsersInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Cell doBCell = row.getCell(0);
            Cell emailCell = row.getCell(1);
            Cell firstNameCell = row.getCell(2);
            Cell genderCell = row.getCell(3);
            Cell lastNameCell = row.getCell(4);
            Cell passwordCell = row.getCell(5);
            Cell phoneCell = row.getCell(6);
            Cell orgCell = row.getCell(7);
            Cell roleCell = row.getCell(8);

            DataFormatter formatter = new DataFormatter();

            if (emailCell != null) {
                Timestamp dOB = Timestamp.valueOf(formatter.formatCellValue(doBCell));
                String email = formatter.formatCellValue(emailCell);
                String firstName = formatter.formatCellValue(firstNameCell);
                String lastName = formatter.formatCellValue(lastNameCell);
                Integer gender = Integer.valueOf(formatter.formatCellValue(genderCell));
                UUID roleId = UUID.fromString(formatter.formatCellValue(roleCell));
                UUID orgId = orgCell != null ? UUID.fromString(formatter.formatCellValue(orgCell)) : null;
                String password = formatter.formatCellValue(passwordCell);
                String phone = formatter.formatCellValue(phoneCell);

                User user = new User();
                try {

                    Organization organization = orgCell != null ? organizationService.findById(orgId).orElse(null) : null;
                    Role role = roleService.findById(roleId).orElse(null);

                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setDateOfBirth(dOB);
                    user.setGender(gender);
                    user.setOrganization(organization);
                    user.setRole(role);

                    userService.save(user);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create users successfully")
                .build());
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUsers() throws IOException {
        FileInputStream file = new FileInputStream("D:/Nam4/Ky1/TieuLuanChuyenNganh/UsersInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Cell emailCell = row.getCell(1);
            Cell passwordCell = row.getCell(5);

            DataFormatter formatter = new DataFormatter();

            if (emailCell != null) {
                String email = formatter.formatCellValue(emailCell);
                String password = formatter.formatCellValue(passwordCell);

                try {
                    User user = userRepository.findByEmail(email).orElse(null);
                    user.setPassword(password);

                    userService.save(user);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update users successfully")
                .build());
    }

    @PutMapping("/auth/123")
    public ResponseEntity<?> refactorUserImage() throws IOException {
        Pageable pageable = PageRequest.of(0, 1000);
        List<User> users = userRepository.findAll(pageable).getContent();

        for (User user : users) {
            if (user.getImage() != null) {
                String url = user.getImage().replace("https://drive.google.com/uc", "https://drive.google.com/thumbnail");
                user.setImage(url);
                userService.update(user);
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update users successfully")
                .build());
    }

    @PutMapping("/auth/456")
    public ResponseEntity<?> refactorDoc() throws IOException {
        Pageable pageable = PageRequest.of(0, 1000);
        List<Document> documents = documentRepository.findAll();

        for (Document document : documents) {
            if (document.getThumbnail() != null) {
                String url = document.getThumbnail().replace("https://drive.google.com/uc", "https://drive.google.com/thumbnail");
                String id = getId(document.getViewUrl());
                document.setThumbnail(url);
                document.setViewUrl("https://drive.google.com/file/d/" + id + "/preview");
                documentRepository.save(document);
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update users successfully")
                .build());
    }

    @PostMapping("/likes")
    public ResponseEntity<?> initLikes() throws IOException {
        FileInputStream file = new FileInputStream("D:/Nam4/Ky1/TieuLuanChuyenNganh/LikesInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Cell userCell = row.getCell(0);
            Cell docCell = row.getCell(1);

            DataFormatter formatter = new DataFormatter();

            if (userCell != null) {
                UUID userId = FastUuidParser.fromString(formatter.formatCellValue(userCell));
                UUID docId = FastUuidParser.fromString(formatter.formatCellValue(docCell));

                DocumentLike documentLike = new DocumentLike();
                try {

                    User user = userRepository.findById(userId).orElse(null);
                    Document document = documentRepository.findById(docId).orElse(null);

                    documentLike.setDocument(document);
                    documentLike.setUser(user);

                    documentLikeRepository.save(documentLike);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create likes successfully")
                .build());
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> initReviews() throws IOException {
        FileInputStream file = new FileInputStream("D:/Nam4/Ky1/TieuLuanChuyenNganh/ReviewsInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Cell userCell = row.getCell(0);
            Cell docCell = row.getCell(1);
            Cell starCell = row.getCell(2);
            Cell contentCell = row.getCell(3);

            DataFormatter formatter = new DataFormatter();

            if (userCell != null) {
                UUID userId = FastUuidParser.fromString(formatter.formatCellValue(userCell));
                UUID docId = FastUuidParser.fromString(formatter.formatCellValue(docCell));
                Integer star = Integer.valueOf(formatter.formatCellValue(starCell));
                String content = formatter.formatCellValue(contentCell);

                Review review = new Review();
                try {

                    User user = userRepository.findById(userId).orElse(null);
                    Document document = documentRepository.findById(docId).orElse(null);

                    review.setUser(user);
                    review.setDocument(document);
                    review.setStar(star);
                    review.setContent(content);

                    reviewRepository.save(review);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create reviews successfully")
                .build());
    }

    @PostMapping("/saves")
    public ResponseEntity<?> initSaves() throws IOException {
        FileInputStream file = new FileInputStream("D:/Nam4/Ky1/TieuLuanChuyenNganh/SavesInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Cell userCell = row.getCell(0);
            Cell docCell = row.getCell(1);

            DataFormatter formatter = new DataFormatter();

            if (userCell != null) {
                UUID userId = FastUuidParser.fromString(formatter.formatCellValue(userCell));
                UUID docId = FastUuidParser.fromString(formatter.formatCellValue(docCell));

                Save save = new Save();
                try {

                    User user = userRepository.findById(userId).orElse(null);
                    Document document = documentRepository.findById(docId).orElse(null);

                    save.setDocument(document);
                    save.setUser(user);

                    saveRepository.save(save);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create likes successfully")
                .build());
    }

    @PostMapping("/recencies")
    public ResponseEntity<?> initRecencies() throws IOException {
        FileInputStream file = new FileInputStream("D:/Nam4/Ky1/TieuLuanChuyenNganh/RecenciesInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Cell userCell = row.getCell(0);
            Cell docCell = row.getCell(1);
            Cell accessCell = row.getCell(2);

            DataFormatter formatter = new DataFormatter();

            if (userCell != null) {
                UUID userId = FastUuidParser.fromString(formatter.formatCellValue(userCell));
                UUID docId = FastUuidParser.fromString(formatter.formatCellValue(docCell));
                Timestamp accessedAt = Timestamp.valueOf(formatter.formatCellValue(accessCell));

                Recency recency = new Recency();
                try {

                    User user = userRepository.findById(userId).orElse(null);
                    Document document = documentRepository.findById(docId).orElse(null);

                    recency.setDocument(document);
                    recency.setAccessedAt(accessedAt);
                    recency.setUser(user);

                    recencyRepository.save(recency);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create recencies successfully")
                .build());
    }

    @PostMapping("/posts")
    public ResponseEntity<?> initPosts() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\PostsInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }
            Cell titleCell = row.getCell(0);
            Cell contentCell = row.getCell(1);
            Cell totalViewsCell = row.getCell(2);
            Cell userCell = row.getCell(3);
            Cell labelCell = row.getCell(4);
            Cell subsectionCell = row.getCell(5);

            DataFormatter formatter = new DataFormatter();

            if (titleCell != null) {
                String title = formatter.formatCellValue(titleCell);
                String content = formatter.formatCellValue(contentCell);
                Integer totalViews = Integer.valueOf(formatter.formatCellValue(totalViewsCell));
                UUID userId = UUID.fromString(formatter.formatCellValue(userCell));
                UUID subId = UUID.fromString(formatter.formatCellValue(subsectionCell));
                UUID labelId = labelCell == null ? null : UUID.fromString(formatter.formatCellValue(labelCell));

                Post post = new Post();
                try {
                    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
                    Subsection subsection = subsectionRepository.findById(subId).orElseThrow(() -> new RuntimeException("Subsection not found"));
                    Label label = labelId == null ? null : labelRepository.findById(labelId).orElseThrow(() -> new RuntimeException("Label not found"));

                    post.setSubsection(subsection);
                    post.setLabel(label);
                    post.setUserPosted(user);
                    post.setContent(content);
                    post.setTitle(title);
                    post.setTotalViews(totalViews);

                    List<String> keywords = tagExtractor.findKeywords(title
                            .concat(". ")
                            .concat(content.replace("<[^>]*>", "")));

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

                        if (!post.getTags().contains(tag)) {
                            post.getTags().add(tag);
                            postRepository.save(post);
                        }
                    }

                    postRepository.save(post);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create posts successfully")
                .build());
    }

    @PostMapping("/reply")
    public ResponseEntity<?> initReplies() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\RepliesInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            Cell idCell = row.getCell(0);
            Cell contentCell = row.getCell(1);
            Cell postCell = row.getCell(2);
            Cell userReplyCell = row.getCell(3);
            Cell parentCell = row.getCell(4);

            DataFormatter formatter = new DataFormatter();

            if (idCell != null) {
                UUID id = UUID.fromString(formatter.formatCellValue(idCell));
                String content = formatter.formatCellValue(contentCell);
                UUID postId = UUID.fromString(formatter.formatCellValue(postCell));
                UUID userReplyId = UUID.fromString(formatter.formatCellValue(userReplyCell));
                UUID parentId = parentCell != null ? UUID.fromString(formatter.formatCellValue(parentCell)) : null;

                Reply reply = new Reply();
                try {
                    User userReply = userRepository.findById(userReplyId).orElseThrow(() -> new RuntimeException("User not found"));
                    Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

                    reply.setReplyId(id);
                    reply.setContent(content);
                    reply.setPost(post);
                    reply.setUser(userReply);

                    replyService.save(reply);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Create replies successfully")
                .build());
    }

    @PutMapping("/reply")
    public ResponseEntity<?> addParentReplies() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\RepliesInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            Cell idCell = row.getCell(0);
            Cell parentCell = row.getCell(4);

            DataFormatter formatter = new DataFormatter();

            if (parentCell != null) {
                UUID id = UUID.fromString(formatter.formatCellValue(idCell));
                UUID parentId = UUID.fromString(formatter.formatCellValue(parentCell));

                try {
                    Reply reply = replyService.findById(id).orElseThrow(() -> new RuntimeException("Reply not found"));
                    Reply parentReply = replyService.findById(parentId).orElseThrow(() -> new RuntimeException("Parent reply not found"));

                    reply.setParentReply(parentReply);

                    System.out.println(row.getRowNum());
                    replyService.save(reply);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add parent reply successfully")
                .build());
    }

    @PostMapping("/replyLike")
    public ResponseEntity<?> addReplyLikes() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\ReplyLikesInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }

            Cell replyCell = row.getCell(0);
            Cell userCell = row.getCell(1);

            DataFormatter formatter = new DataFormatter();

            if (replyCell != null) {
                UUID replyId = UUID.fromString(formatter.formatCellValue(replyCell));
                UUID userId = UUID.fromString(formatter.formatCellValue(userCell));

                try {
                    Reply reply = replyService.findById(replyId).orElseThrow(() -> new RuntimeException("Reply not found"));
                    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

                    ReplyLike replyLike = new ReplyLike();
                    replyLike.setReply(reply);
                    replyLike.setUser(user);

                    replyLikeService.save(replyLike);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add reply likes successfully")
                .build());
    }

    @PostMapping("/postLike")
    public ResponseEntity<?> addPostLikes() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\PostLikesInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }

            Cell postCell = row.getCell(0);
            Cell userCell = row.getCell(1);

            DataFormatter formatter = new DataFormatter();

            if (postCell != null) {
                UUID postId = UUID.fromString(formatter.formatCellValue(postCell));
                UUID userId = UUID.fromString(formatter.formatCellValue(userCell));

                try {
                    Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
                    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

                    PostLike postLike = new PostLike();
                    postLike.setPost(post);
                    postLike.setUser(user);

                    postLikeService.save(postLike);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add reply likes successfully")
                .build());
    }

    @PostMapping("/subsections")
    public ResponseEntity<?> addSubsections() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\SubsectionsInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }

            Cell cellName = row.getCell(0);
            Cell cellSection = row.getCell(1);
            Cell cellSlug = row.getCell(2);

            DataFormatter formatter = new DataFormatter();

            if (cellName != null) {
                UUID sectionId = UUID.fromString(formatter.formatCellValue(cellSection));
                String name = formatter.formatCellValue(cellName);
                String slug = formatter.formatCellValue(cellSlug);

                try {
                    Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new RuntimeException("Reply not found"));
                    Subsection subsection = new Subsection();
                    subsection.setSlug(slug);
                    subsection.setSubName(name);
                    subsection.setSection(section);
                    subsectionRepository.save(subsection);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add subsections successfully")
                .build());
    }

    @PostMapping("/labels")
    public ResponseEntity<?> addLabels() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\LabelsInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }

            Cell cellName = row.getCell(0);
            Cell cellSlug = row.getCell(1);

            DataFormatter formatter = new DataFormatter();

            if (cellName != null) {
                String name = formatter.formatCellValue(cellName);
                String slug = formatter.formatCellValue(cellSlug);

                try {
                    Label label = new Label();
                    label.setLabelName(name);
                    label.setSlug(slug);
                    labelRepository.save(label);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add labels successfully")
                .build());
    }

    @PutMapping("/post/labelsubsection")
    public ResponseEntity<?> addLabelAndSubsectionForPost() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\PostLabelSubsectionInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }

            Cell cellPost = row.getCell(0);
            Cell cellSubsection = row.getCell(1);
            Cell cellLabel = row.getCell(2);

            DataFormatter formatter = new DataFormatter();

            if (cellPost != null) {
                UUID postId = UUID.fromString(formatter.formatCellValue(cellPost));
                UUID subId = UUID.fromString(formatter.formatCellValue(cellSubsection));
                UUID labelId = cellLabel == null ? null : UUID.fromString(formatter.formatCellValue(cellLabel));

                try {
                    Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
                    Subsection subsection = subsectionRepository.findById(subId).orElseThrow(() -> new RuntimeException("Subsection not found"));
                    Label label = labelId == null ? null : labelRepository.findById(labelId).orElseThrow(() -> new RuntimeException("Label not found"));

                    post.setSubsection(subsection);
                    post.setLabel(label);
                    postRepository.save(post);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add labels and subsections for posts successfully")
                .build());
    }

    @PostMapping("/badgeTypes")
    public ResponseEntity<?> addBadgeTypes() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\BadgesInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(1);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }

            Cell cellUnit = row.getCell(0);

            DataFormatter formatter = new DataFormatter();

            if (cellUnit != null) {
                String unit = formatter.formatCellValue(cellUnit);

                try {
                    BadgeType badgeType = new BadgeType();
                    badgeType.setUnit(unit);
                    badgeTypeRepository.save(badgeType);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add badge type successfully")
                .build());
    }

    @PostMapping("/badges")
    public ResponseEntity<?> addBadges() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\BadgesInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }

            Cell cellName = row.getCell(0);
            Cell cellValue = row.getCell(1);
            Cell cellType = row.getCell(2);

            DataFormatter formatter = new DataFormatter();

            if (cellName != null) {
                String name = formatter.formatCellValue(cellName);
                int value = Integer.parseInt(formatter.formatCellValue(cellValue));
                UUID type = UUID.fromString(formatter.formatCellValue(cellType));

                try {
                    BadgeType badgeType = badgeTypeRepository.findById(type).orElse(null);
                    Badge badge = new Badge();
                    badge.setBadgeName(name);
                    badge.setBadgeType(badgeType);
                    badge.setValue(value);
                    badgeRepository.save(badge);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add badges successfully")
                .build());
    }

    @PostMapping("/badges/all")
    public ResponseEntity<?> rewardBadgesForAll() {
        List<User> users = userRepositoty.findAll();
        List<BadgeType> badgeTypes = badgeTypeRepository.findAll();
        for (User user : users) {
            for (BadgeType badgeType : badgeTypes) {
                int value = 0;
                if (badgeType.getUnit().equals(BadgeUnit.TOTAL_POSTS.name()))
                    value = user.getPosts().size();
                else if (badgeType.getUnit().equals(BadgeUnit.TOTAL_REPLIES.name()))
                    value = user.getReplies().size();
                else if (badgeType.getUnit().equals(BadgeUnit.TOTAL_POST_LIKES.name()))
                    value = user.getPosts().stream()
                            .flatMapToInt(post -> IntStream.of(post.getPostLikes().size()))
                            .sum();
                else if (badgeType.getUnit().equals(BadgeUnit.TOTAL_REPLY_LIKES.name()))
                    value = user.getReplies().stream()
                            .flatMapToInt(reply -> IntStream.of(reply.getReplyLikes().size()))
                            .sum();
                else
                    value = user.getPosts().stream().mapToInt(Post::getTotalViews).sum();

                List<Badge> badges = badgeRepository.findByBadgeTypeAndValueLessThanEqual(badgeType, value);
                for (Badge badge : badges) {
                    BadgeReward badgeReward = new BadgeReward();
                    badgeReward.setBadge(badge);
                    badgeReward.setUser(user);
                    badgeRewardRepository.save(badgeReward);
                }
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add badges successfully")
                .build());
    }

    @PostMapping("/tags/post")
    public ResponseEntity<?> addTagsForPost() {
        List<Post> posts = postRepository.findAll();
        for (Post post : posts) {
            String filteredContent = post.getContent().replaceAll("<[^>]*>", "");
            try {
                List<String> keywords = tagExtractor.findKeywords(post.getTitle().concat(". ").concat(filteredContent));
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

                    if (!post.getTags().contains(tag)) {
                        post.getTags().add(tag);
                        postRepository.save(post);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        test();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add tags for post successfully")
                .build());
    }

    public void test() {
        List<String> tags = Arrays.asList("ngôn ngữ", "ngôn ngữ tự nhiên", "ai", "ml");
        Post post = postRepository.findById(UUID.fromString("c0a80064-8e78-115a-818e-78e76d6c0004")).get();

        tags.forEach(tag -> {
            if (tagRepository.existsByTagName(tag)) {
                Tag tag1 = tagRepository.findByTagName(tag).orElse(null);

                if (!post.getTags().stream().map(t -> t.getTagId()).collect(Collectors.toList()).contains(tag1.getTagId())) {
                    post.getTags().add(tag1);
                    postRepository.save(post);
                }
            } else {
                Tag tag1 = new Tag();
                tag1.setTagName(tag);
                tag1.setSlug(SlugGenerator.generateSlug(tag, false));
                tag1 = tagRepository.save(tag1);

                if (!post.getTags().stream().map(t -> t.getTagId()).collect(Collectors.toList()).contains(tag1.getTagId())) {
                    post.getTags().add(tag1);
                    postRepository.save(post);
                }
            }
        });

        List<Post> posts = postRepository.findAll();
        posts.forEach(post1 -> {
            if (post1.getPostHistories().size() == 0)
                post1.setUpdatedAt(null);
            postRepository.save(post1);
        });
    }

    @PostMapping("/tags/document")
    public ResponseEntity<?> addTagsForDocument() {
        List<Document> documents = documentRepository.findAll();
        for (Document document : documents) {
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

                    if (!document.getTags().contains(tag)) {
                        document.getTags().add(tag);
                        documentRepository.save(document);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add tags for document successfully")
                .build());
    }

    @PostMapping("/document/share")
    public ResponseEntity<?> refactorShare() {
        List<Document> documents = documentRepository.findAll();
        for (Document document : documents) {
            if (document.getUserUploaded().getRole().getRoleName().equals("ROLE_STUDENT")) {
                document.setContributed(true);
                documentRepository.save(document);
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Refactor share for document successfully")
                .build());
    }

    @PostMapping("/user/lecturer")
    public ResponseEntity<?> refactorLecturers() {
        List<User> users = userRepositoty.findAll();
        for (User user : users) {
            if (user.getRole().getRoleName().equals("ROLE_LECTURER")) {
                Role role = roleService.findById(UUID.fromString("c0a801b9-8ac0-1a60-818a-c04a8f950035")).orElse(null);
                user.setRole(role);
                userRepositoty.save(user);
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Refactor lecturer successfully")
                .build());
    }

    @PostMapping("/collections")
    public ResponseEntity<?> addCollections() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\CollectionsInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }

            Cell cellName = row.getCell(0);
            Cell cellUser = row.getCell(1);

            DataFormatter formatter = new DataFormatter();

            if (cellName != null) {
                String name = formatter.formatCellValue(cellName);
                UUID userId = UUID.fromString(formatter.formatCellValue(cellUser));

                try {
                    User user = userRepository.findById(userId).orElse(null);

                    Collection collection = new Collection();
                    collection.setCollectionName(name);
                    collection.setUser(user);
                    collectionRepository.save(collection);
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add collections successfully")
                .build());
    }

    @PostMapping("/collections/document")
    public ResponseEntity<?> addDocumentsToCollection() throws IOException {
        FileInputStream file = new FileInputStream("D:\\Nam4\\Ky1\\TieuLuanChuyenNganh\\digital-library\\src\\main\\resources\\database\\CollectionsInit.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(1);

        // Duyệt qua các hàng trong sheet
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue;
            }

            Cell cellCollection = row.getCell(0);
            Cell cellDocument = row.getCell(1);

            DataFormatter formatter = new DataFormatter();

            if (cellCollection != null) {
                UUID collectionId = UUID.fromString(formatter.formatCellValue(cellCollection));
                UUID docId = UUID.fromString(formatter.formatCellValue(cellDocument));

                try {
                    Collection collection = collectionRepository.findById(collectionId).orElse(null);
                    Document document = documentRepository.findById(docId).orElse(null);

                    CollectionDocument collectionDocument = new CollectionDocument();
                    collectionDocument.setCollection(collection);
                    collectionDocument.setDocument(document);
                    collectionDocumentRepository.save(collectionDocument);

                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }

        workbook.close();
        file.close();

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Add documents for collection successfully")
                .build());
    }

    @PostMapping("/notifications")
    public ResponseEntity<?> initNotifications() {
        List<PostLike> postLikes = postLikeRepository.findAll();
        List<ReplyLike> replyLikes = replyLikeRepository.findAll();
        List<Reply> replies = replyRepository.findAll();
        List<User> users = userRepositoty.findAll().stream().filter(u -> u.getRole().getRoleName().equals("ROLE_STUDENT")).collect(Collectors.toList());
        List<BadgeReward> badges = badgeRewardRepository.findAll();
        List<Review> reviews = reviewRepository.findAll();
        List<Document> documents = documentRepository.findAll();

        for (PostLike postLike : postLikes) {
            if (!postLike.getUser().getUserId().equals(postLike.getPost().getUserPosted().getUserId())) {
                Notification notification = new Notification();
                notification.setPost(postLike.getPost());
                notification.setSender(postLike.getUser());
                notification.setRecipient(postLike.getPost().getUserPosted());
                notification.setSentAt(postLike.getLikedAt());
                notification.setType(NotificationMessage.LIKE_POST.name());
                notification.setMessage(NotificationMessage.LIKE_POST.getMessage());
                notificationRepository.save(notification);
            }
        }
        for (ReplyLike replyLike : replyLikes) {
            if (!replyLike.getUser().getUserId().equals(replyLike.getReply().getUser().getUserId())) {
                Notification notification = new Notification();
                notification.setReply(replyLike.getReply());
                notification.setSender(replyLike.getUser());
                notification.setRecipient(replyLike.getReply().getUser());
                notification.setSentAt(replyLike.getLikedAt());
                notification.setType(NotificationMessage.LIKE_REPLY.name());
                notification.setMessage(NotificationMessage.LIKE_REPLY.getMessage());
                notificationRepository.save(notification);
            }
        }
        for (Reply reply : replies) {
            if (!reply.getUser().getUserId().equals(reply.getPost().getUserPosted().getUserId())) {
                Notification notification = new Notification();
                notification.setReply(reply);
                notification.setSender(reply.getUser());
                notification.setRecipient(reply.getPost().getUserPosted());
                notification.setSentAt(reply.getCreatedAt());
                notification.setType(NotificationMessage.REPLY.name());
                notification.setMessage(NotificationMessage.REPLY.getMessage());
                notificationRepository.save(notification);
            }
        }
        for (BadgeReward badge : badges) {
            Notification notification = new Notification();
            notification.setBadge(badge.getBadge());
            notification.setSender(null);
            notification.setRecipient(badge.getUser());
            notification.setSentAt(badge.getRewardedAt());
            notification.setType(NotificationMessage.REWARD_BADGE.name());
            notification.setMessage(NotificationMessage.REWARD_BADGE.getMessage());
            notificationRepository.save(notification);
        }
        for (Document document : documents) {
            if (document.getVerifiedStatus() != 0) {
                Notification notification = new Notification();
                notification.setDocument(document);
                notification.setSender(document.getUserVerified());
                notification.setRecipient(document.getUserUploaded());
                notification.setSentAt(document.getVerifiedAt());
                if (document.getVerifiedStatus() == 1) {
                    notification.setType(NotificationMessage.ACCEPT_DOCUMENT.name());
                    notification.setMessage(NotificationMessage.ACCEPT_DOCUMENT.getMessage());
                } else {
                    notification.setType(NotificationMessage.REJECT_DOCUMENT.name());
                    notification.setMessage(NotificationMessage.REJECT_DOCUMENT.getMessage());
                }
                notificationRepository.save(notification);
            }
        }
        for (Review review : reviews) {
            if (review.getVerifiedStatus() != 0) {
                Notification notification = new Notification();
                notification.setReview(review);
                notification.setSender(review.getUserVerified());
                notification.setRecipient(review.getUser());
                notification.setSentAt(review.getVerifiedAt());
                if (review.getVerifiedStatus() == 1) {
                    notification.setType(NotificationMessage.ACCEPT_REVIEW.name());
                    notification.setMessage(NotificationMessage.ACCEPT_REVIEW.getMessage());
                } else {
                    notification.setType(NotificationMessage.REJECT_REVIEW.name());
                    notification.setMessage(NotificationMessage.REJECT_REVIEW.getMessage());
                }
                notificationRepository.save(notification);
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Init notifications successfully")
                .build());
    }

    @PostMapping("/post_reply_updated")
    public ResponseEntity<?> resetPostAndReplyUpdated() {
        List<Post> posts = postRepository.findAll();
        List<Reply> replies = replyRepository.findAll();

//        for (Post post : posts) {
//            if (post.getPostHistories().isEmpty()) {
//                post.setUpdatedAt(null);
//                postRepository.save(post);
//            } else {
//                for (PostHistory postHistory : post.getPostHistories()) {
//                    if (postHistory.getLoggedAt().before(post.getCreatedAt())) {
//                        long minOffset = 1 * 24 * 60 * 60 * 1000; // 1 ngày
//                        long maxOffset = 10 * 24 * 60 * 60 * 1000; // 10 ngày
//                        // Tạo khoảng thời gian ngẫu nhiên trong khoảng từ minOffset đến maxOffset
//                        long randomOffset = ThreadLocalRandom.current().nextLong(minOffset, maxOffset + 1);
//
//                        postHistory.setLoggedAt(new Timestamp(post.getCreatedAt().getTime() + randomOffset));
//                        postHistoryRepository.save(postHistory);
//                    }
//                }
//                PostHistory latestP = post.getPostHistories().stream()
//                        .max(Comparator.comparing(PostHistory::getLoggedAt)).get();
//                post.setUpdatedAt(latestP.getLoggedAt());
//                postRepository.save(post);
//            }
//        }
//
//        for (Post post : posts) {
//            if (!post.getReplies().isEmpty()) {
//                List<Reply> sortedReplyList = post.getReplies().stream()
//                        .sorted(Comparator.comparing(Reply::getCreatedAt))
//                        .collect(Collectors.toList());
//                Reply earliestReply = sortedReplyList.get(0);
//                for (Reply sortedReply : sortedReplyList) {
//                    sortedReply.setCreatedAt(new Timestamp(post.getCreatedAt().getTime()
//                            + (sortedReply.getCreatedAt().getTime()
//                            - earliestReply.getCreatedAt().getTime())));
//
//                    if (sortedReply.getReplyHistories().isEmpty()) {
//                        sortedReply.setUpdatedAt(null);
//                    }
//                    replyRepository.save(sortedReply);
//                }
//            }
//        }

        for (Reply reply : replies) {
            if (reply.getReplyHistories().isEmpty()) {
                reply.setUpdatedAt(null);
                replyRepository.save(reply);
            } else {
                for (ReplyHistory replyHistory : reply.getReplyHistories()) {
                    if (replyHistory.getLoggedAt().before(reply.getCreatedAt())) {
                        long minOffset = 1 * 24 * 60 * 60 * 1000; // 1 ngày
                        long maxOffset = 10 * 24 * 60 * 60 * 1000; // 10 ngày
                        // Tạo khoảng thời gian ngẫu nhiên trong khoảng từ minOffset đến maxOffset
                        long randomOffset = ThreadLocalRandom.current().nextLong(minOffset, maxOffset + 1);

                        replyHistory.setLoggedAt(new Timestamp(reply.getCreatedAt().getTime() + randomOffset));
                        replyHistoryRepository.save(replyHistory);
                    }
                }
                ReplyHistory latestP = reply.getReplyHistories().stream()
                        .max(Comparator.comparing(ReplyHistory::getLoggedAt)).get();
                reply.setUpdatedAt(latestP.getLoggedAt());
                replyRepository.save(reply);
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Reset updated successfully")
                .build());
    }

    @PostMapping("/post_likes_time")
    public ResponseEntity<?> initPostLikesTime() {
        List<Post> posts = postRepository.findAll();

        for (Post post : posts) {
            if (!post.getPostLikes().isEmpty()) {
                for (PostLike postLike : post.getPostLikes()) {
                    long minOffset = 1 * 24 * 60 * 60 * 1000; // 1 ngày
                    long maxOffset = 10 * 24 * 60 * 60 * 1000; // 10 ngày
                    // Tạo khoảng thời gian ngẫu nhiên trong khoảng từ minOffset đến maxOffset
                    long randomOffset = ThreadLocalRandom.current().nextLong(minOffset, maxOffset + 1);

                    postLike.setLikedAt(new Timestamp(post.getCreatedAt().getTime() + randomOffset));
                    postLikeRepository.save(postLike);
                }
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Reset post like times successfully")
                .build());
    }

    @PostMapping("/reply_likes_time")
    public ResponseEntity<?> initReplyLikesTime() {
        List<Reply> replies = replyRepository.findAll();

        for (Reply post : replies) {
            if (!post.getReplyLikes().isEmpty()) {
                for (ReplyLike replyLike : post.getReplyLikes()) {
                    long minOffset = 1 * 24 * 60 * 60 * 1000; // 1 ngày
                    long maxOffset = 10 * 24 * 60 * 60 * 1000; // 10 ngày
                    // Tạo khoảng thời gian ngẫu nhiên trong khoảng từ minOffset đến maxOffset
                    long randomOffset = ThreadLocalRandom.current().nextLong(minOffset, maxOffset + 1);

                    replyLike.setLikedAt(new Timestamp(post.getCreatedAt().getTime() + randomOffset));
                    replyLikeRepository.save(replyLike);
                }
            }
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Reset reply like times successfully")
                .build());
    }

    @PostMapping("/document_verified_time")
    public ResponseEntity<?> initDocVerifiedTime() {
        List<Document> documents = documentRepository.findAll();

        for (Document document : documents) {
            if (document.getUserUploaded().getRole().getRoleName().equals("ROLE_STUDENT")) {
                if (document.getVerifiedStatus() != 0) {
                    long minOffset = 1 * 1 * 60 * 60 * 1000; // 1 giờ
                    long maxOffset = 2 * 24 * 60 * 60 * 1000; // 2 ngày
                    // Tạo khoảng thời gian ngẫu nhiên trong khoảng từ minOffset đến maxOffset
                    long randomOffset = ThreadLocalRandom.current().nextLong(minOffset, maxOffset + 1);

                    document.setVerifiedAt(new Timestamp(document.getUploadedAt().getTime() + randomOffset));
                } else {
                    document.setVerifiedAt(null);
                    document.setUserVerified(null);
                }
            } else {
                document.setVerifiedAt(new Timestamp(document.getUploadedAt().getTime() + 10000));
                document.setUserVerified(document.getUserUploaded());
            }
            documentRepository.save(document);
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Init doc verified successfully")
                .build());
    }

    @PostMapping("/review_verified_time")
    public ResponseEntity<?> initReviewVerifiedTime() {
        List<Review> reviews = reviewRepository.findAll();

        // Thiết lập createdAt cho từng review
        for (Review review : reviews) {
            long minOffset = 2 * 24 * 60 * 60 * 1000L; // 2 ngày
            long maxOffset = 30 * 24 * 60 * 60 * 1000L; // 30 ngày
            long randomOffset = ThreadLocalRandom.current().nextLong(minOffset, maxOffset + 1);
            review.setCreatedAt(new Timestamp(review.getDocument().getUploadedAt().getTime() + randomOffset));
            reviewRepository.save(review);
        }

        // Thiết lập updatedAt cho từng review
        for (Review review : reviews) {
            long minOffset = 12 * 60 * 60 * 1000L; // 12 giờ
            long maxOffset = 10 * 24 * 60 * 60 * 1000L; // 10 ngày
            long randomOffset = ThreadLocalRandom.current().nextLong(minOffset, maxOffset + 1);
            review.setUpdatedAt(new Timestamp(review.getCreatedAt().getTime() + randomOffset));
            reviewRepository.save(review);
        }

        // Thiết lập verifiedAt cho từng review dựa trên verifiedStatus
        for (Review review : reviews) {
            if (review.getVerifiedStatus() != 0) {
                long minOffset = 1 * 60 * 60 * 1000L; // 1 giờ
                long maxOffset = 2 * 24 * 60 * 60 * 1000L; // 2 ngày
                long randomOffset = ThreadLocalRandom.current().nextLong(minOffset, maxOffset + 1);
                review.setVerifiedAt(new Timestamp(review.getUpdatedAt().getTime() + randomOffset));
            } else {
                review.setVerifiedAt(null);
                review.setUserVerified(null);
            }
            reviewRepository.save(review);
        }

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Init doc verified successfully")
                .build());
    }

    private String getId(String url) {
        Pattern pattern = Pattern.compile("id=([^&]+)");
        Matcher matcher = pattern.matcher(url);

        // Tìm và trích xuất ID từ đường link
        String id = "";
        if (matcher.find()) {
            id = matcher.group(1);
        }

        return id;
    }
}
