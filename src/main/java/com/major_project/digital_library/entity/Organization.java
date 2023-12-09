package com.major_project.digital_library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Organization {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID orgId;

    @Column(unique = true, length = 100, nullable = false)
    private String orgName;

    @Column(unique = true, length = 100, nullable = false)
    private String slug;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private boolean isDeleted;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "organization")
    private List<Document> documents = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreRemove
    protected void onRemove() {
        isDeleted = true;
    }

//    @PostConstruct
//    public void initialize() {
//        Organization o1 = new Organization();
//        o1.setOrgName("Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh");
//        organizationService.save(o1);
//
//        Organization o2 = new Organization();
//        o1.setOrgName("Trường Đại học Kinh tế TP. Hồ Chí Minh");
//        organizationService.save(o2);
//
//        Organization o3 = new Organization();
//        o1.setOrgName("Trường Đại học Khoa học Tự nhiên - ĐHQG TP Hồ Chí Minh");
//        organizationService.save(o3);
//
//        Organization o4 = new Organization();
//        o1.setOrgName("Trường Đại học Khoa học Xã hội và Nhân văn - ĐHQG TP.HCM");
//        organizationService.save(o4);
//
//        Organization o5 = new Organization();
//        o1.setOrgName("Trường Đại học Kiến trúc Thành phố Hồ Chí Minh");
//        organizationService.save(o5);
//
//        Organization o6 = new Organization();
//        o1.setOrgName("Trường Đại học Công nghệ Thông tin - ĐH Quốc gia TP.HCM");
//        organizationService.save(o6);
//
//        Organization o7 = new Organization();
//        o1.setOrgName("Trường Đại học Bách Khoa - ĐHQG TP.HCM");
//        organizationService.save(o7);
//
//        Organization o8 = new Organization();
//        o1.setOrgName("Trường Đại học Giao thông vận tải Thành phố Hồ Chí Minh");
//        organizationService.save(o8);
//
//        Organization o9 = new Organization();
//        o1.setOrgName("Trường Đại học Mở Thành phố Hồ Chí Minh");
//        organizationService.save(o9);
//
//        Organization o10 = new Organization();
//        o1.setOrgName("Trường Đại học Luật TP.HCM");
//        organizationService.save(o10);
//
//        Organization o11 = new Organization();
//        o1.setOrgName("Trường Đại học Ngân hàng TP. HCM");
//        organizationService.save(o11);
//
//        Organization o12 = new Organization();
//        o1.setOrgName("Trường Đại học Nông Lâm Thành phố Hồ Chí Minh");
//        organizationService.save(o12);
//
//        Organization o13 = new Organization();
//        o1.setOrgName("Trường Đại học Tôn Đức Thắng");
//        organizationService.save(o13);
//
//        Organization o14 = new Organization();
//        o1.setOrgName("Trường Đại học Ngoại thương (Cơ sở TP.HCM)");
//        organizationService.save(o14);
//
//        Organization o15 = new Organization();
//        o1.setOrgName("Trường Đại học Công thương Thành phố Hồ Chí Minh");
//        organizationService.save(o15);
//
//        Organization o16 = new Organization();
//        o1.setOrgName("Trường Đại học Công nghệ Thành phố Hồ Chí Minh");
//        organizationService.save(o16);
//
//        Organization o17 = new Organization();
//        o1.setOrgName("Học viện Công nghệ Bưu chính Viễn thông (Cơ sở phía Nam)");
//        organizationService.save(o17);
//
//        Organization o18 = new Organization();
//        o1.setOrgName("Trường Đại học Kinh tế - Tài chính TP.HCM");
//        organizationService.save(o18);
//
//        Organization o19 = new Organization();
//        o1.setOrgName("Trường Đại học Ngoại ngữ - Tin học TP.HCM");
//        organizationService.save(o19);
//
//        Organization o20 = new Organization();
//        o1.setOrgName("Đại học Tài chính - Marketing");
//        organizationService.save(o20);
//
//        Organization o21 = new Organization();
//        o1.setOrgName("Trường Cao đẳng Kỹ thuật Cao Thắng");
//        organizationService.save(o21);
//
//        Organization o22 = new Organization();
//        o1.setOrgName("Trường Cao Đẳng FPT Polytechnic TP Hồ Chí Minh");
//        organizationService.save(o22);
//
//        Organization o23 = new Organization();
//        o1.setOrgName("Trường Cao đẳng Kinh tế Thành phố Hồ Chí Minh");
//        organizationService.save(o23);
//
//        Organization o24 = new Organization();
//        o1.setOrgName("Trường Cao đẳng Bách Khoa Sài Gòn");
//        organizationService.save(o24);
//
//        Organization o25 = new Organization();
//        o1.setOrgName("Trường Cao đẳng Công thương TPHCM");
//        organizationService.save(o25);
//
//        Organization o26 = new Organization();
//        o1.setOrgName("Trường Cao đẳng Quốc Tế TP. HCM");
//        organizationService.save(o26);
//
//        Organization o27 = new Organization();
//        o1.setOrgName("Trường Cao đẳng Bách Khoa Nam Sài Gòn");
//        organizationService.save(o27);
//
//        Organization o28 = new Organization();
//        o1.setOrgName("Trường Cao đẳng Công Nghệ Thông Tin TP.HCM");
//        organizationService.save(o28);
//
////        Organization o29 = new Organization();
////        o1.setOrgName("Trường Đại học Ngoại ngữ - Tin học TP.HCM");
////        organizationService.save(o29);
////
////        Organization o30 = new Organization();
////        o1.setOrgName("Đại học Tài chính - Marketing");
////        organizationService.save(o30);
//
//    }
}
