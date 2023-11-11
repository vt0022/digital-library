package com.major_project.digital_library.data_initializer;

import com.major_project.digital_library.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryInitializer implements CommandLineRunner{
    @Autowired
    private ICategoryService categoryService;

    @Override
    public void run(String... args) throws Exception {
//        Category c1 = new Category();
//        c1.setCategoryName("Giáo trình");
//        c1.setSlug("giao-trinh");
//        categoryService.save(c1);
//
//        Category c2 = new Category();
//        c2.setCategoryName("Luận văn, luận án");
//        c2.setSlug("luan-van-luan-an");
//        categoryService.save(c2);
//
//        Category c3 = new Category();
//        c3.setCategoryName("Báo cáo, đồ án");
//        c3.setSlug("bao-cao-do-an");
//        categoryService.save(c3);
//
//        Category c4 = new Category();
//        c4.setCategoryName("Báo cáo nghiên cứu khoa học");
//        c4.setSlug("bao-cao-nghien-cuu-khoa-hoc");
//        categoryService.save(c4);
//
//        Category c5 = new Category();
//        c5.setCategoryName("Đề thi");
//        c5.setSlug("de-thi");
//        categoryService.save(c5);
//
//        Category c6 = new Category();
//        c6.setCategoryName("Tài liệu học tập khác");
//        c6.setSlug("tai-lieu-hoc-tap-khac");
//        categoryService.save(c6);
//
//        Category c7 = new Category();
//        c7.setCategoryName("Sách");
//        c7.setSlug("sach");
//        categoryService.save(c7);
//
//        Category c8 = new Category();
//        c8.setCategoryName("Biểu mẫu - Văn bản");
//        c8.setSlug("bieu-mau-van-ban");
//        categoryService.save(c8);
    }
}
