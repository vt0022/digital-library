package com.major_project.digital_library.controller;

import com.major_project.digital_library.entity.*;
import com.major_project.digital_library.model.FileModel;
import com.major_project.digital_library.model.response_model.ResponseModel;
import com.major_project.digital_library.service.*;
import com.major_project.digital_library.util.GoogleDriveUpload;
import com.major_project.digital_library.util.SlugGenerator;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.komamitsu.fastuuidparser.FastUuidParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/init")
public class InitController {
    private final IUserService userService;
    private final IDocumentService documentService;
    private final IFieldService fieldService;
    private final ICategoryService categoryService;
    private final IOrganizationService organizationService;
    private final IRoleService roleService;
    private final ISaveService saveService;
    private final IReviewService reviewService;
    private final IFavoriteService favoriteService;
    private final GoogleDriveUpload googleDriveUpload;
    private final ModelMapper modelMapper;
    private final SlugGenerator slugGenerator;

    @Autowired
    public InitController(IUserService userService, IDocumentService documentService, IFieldService fieldService, ICategoryService categoryService, IOrganizationService organizationService, IRoleService roleService, ISaveService saveService, IReviewService reviewService, IFavoriteService favoriteService, GoogleDriveUpload googleDriveUpload, ModelMapper modelMapper, SlugGenerator slugGenerator) {
        this.userService = userService;
        this.documentService = documentService;
        this.fieldService = fieldService;
        this.categoryService = categoryService;
        this.organizationService = organizationService;
        this.roleService = roleService;
        this.saveService = saveService;
        this.reviewService = reviewService;
        this.favoriteService = favoriteService;
        this.googleDriveUpload = googleDriveUpload;
        this.modelMapper = modelMapper;
        this.slugGenerator = slugGenerator;
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
                    FileModel gd = googleDriveUpload.uploadFile(multipartFile, docFile.getName(), null, null);

                    // Find
                    Category category = categoryService.findById(categoryId).orElse(null);
                    Field field = fieldService.findById(fieldId).orElse(null);
                    Organization organization = organizationService.findById(orgId).orElse(null);
                    User userUploaded = userService.findById(uploadedBy).orElse(null);
                    User userVerified = userService.findById(verifiedBy).orElse(null);

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

                    documentService.save(document);
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
                    user.setPhone(phone);
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
                    User user = userService.findByEmail(email).orElse(null);
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

                Favorite favorite = new Favorite();
                try {

                    User user = userService.findById(userId).orElse(null);
                    Document document = documentService.findById(docId).orElse(null);

                    favorite.setLiked(true);
                    favorite.setDocument(document);
                    favorite.setUser(user);

                    favoriteService.save(favorite);
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

                    User user = userService.findById(userId).orElse(null);
                    Document document = documentService.findById(docId).orElse(null);

                    review.setUser(user);
                    review.setDocument(document);
                    review.setStar(star);
                    review.setContent(content);

                    reviewService.save(review);
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

                    User user = userService.findById(userId).orElse(null);
                    Document document = documentService.findById(docId).orElse(null);

                    save.setSaved(true);
                    save.setDocument(document);
                    save.setUser(user);

                    saveService.save(save);
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

    @PostMapping("/refactor")
    public ResponseEntity<?> editDocumentsWithQuantity() {
        List<Document> documents = documentService.findAll();

        List<Document> updatedDocuments = new ArrayList<>();

        for (Document document : documents) {
            int totalLikes = document.getFavorites().size();
            List<Review> reviewList = document.getReviews();
            int totalRating = 0;
            for (Review review : reviewList) totalRating += review.getStar();
            double averageRating = reviewList.size() != 0 ? totalRating / reviewList.size() : 0.0;

            document.setTotalFavorite(totalLikes);
            document.setAverageRating(averageRating);

            updatedDocuments.add(document);
        }

        documentService.saveAll(updatedDocuments);

        return ResponseEntity.ok(ResponseModel.builder()
                .status(200)
                .error(false)
                .message("Update total like and average rating successfully")
                .build());
    }
}
