package com.major_project.digital_library.data_initializer;

import com.major_project.digital_library.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class UserInitializer implements CommandLineRunner {
    @Autowired
    private IUserService userService;

    @Override
    public void run(String... args) throws Exception {
//        User user1 = new User();
//        user1.setLastName("Nguyễn");
//        user1.setMiddleName("Văn");
//        user1.setFirstName("Thuận");
//        user1.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user1.setGender(1); // Giả sử 1 là nam
//        user1.setPhone("0987654321");
//        user1.setUsername("vanthuan2724");
//        user1.setPassword("vanthuan2724");
//        user1.setEmail("vanthuan2724@gmail.com");
//        user1.setVerified(true);
//        userService.save(user1);
//
//        User user2 = new User();
//        user2.setLastName("Trần");
//        user2.setMiddleName("Thị");
//        user2.setFirstName("Hằng");
//        user2.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user2.setGender(2); // Giả sử 2 là nữ
//        user2.setPhone("0976543210");
//        user2.setUsername("tran_hang");
//        user2.setPassword("tran_hang0976543210");
//        user2.setEmail("tran.hang@example.com");
//        user2.setVerified(true);
//        userService.save(user2);
//
//        User user3 = new User();
//        user3.setLastName("Lê");
//        user3.setMiddleName("Quốc");
//        user3.setFirstName("Tú");
//        user3.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user3.setGender(1); // Giả sử 1 là nam
//        user3.setPhone("0965432109");
//        user3.setUsername("le_quoc_tu");
//        user3.setPassword("le_quoc_tu0965432109"); // Mật khẩu dựa trên tên và số điện thoại
//        user3.setEmail("le.quoc.tu@example.com");
//        user3.setVerified(true);
//        userService.save(user3);
//
//        User user4 = new User();
//        user4.setLastName("Phạm");
//        user4.setMiddleName("Hữu");
//        user4.setFirstName("Nam");
//        user4.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user4.setGender(1); // Giả sử 1 là nam
//        user4.setPhone("0987654322");
//        user4.setUsername("pham_nam");
//        user4.setPassword("pham_nam0987654322"); // Mật khẩu dựa trên tên và số điện thoại
//        user4.setEmail("pham.nam@example.com");
//        user4.setVerified(true);
//        userService.save(user4);
//
//        User user5 = new User();
//        user5.setLastName("Hoàng");
//        user5.setMiddleName("Thị");
//        user5.setFirstName("Linh");
//        user5.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user5.setGender(2); // Giả sử 2 là nữ
//        user5.setPhone("0976543211");
//        user5.setUsername("hoang_linh");
//        user5.setPassword("hoang_linh0976543211"); // Mật khẩu dựa trên tên và số điện thoại
//        user5.setEmail("hoang.linh@example.com");
//        user5.setVerified(true);
//        userService.save(user5);
//
//        User user6 = new User();
//        user6.setLastName("Vũ");
//        user6.setMiddleName("Quang");
//        user6.setFirstName("Huy");
//        user6.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user6.setGender(1); // Giả sử 1 là nam
//        user6.setPhone("0965432110");
//        user6.setUsername("vu_huy");
//        user6.setPassword("vu_huy0965432110"); // Mật khẩu dựa trên tên và số điện thoại
//        user6.setEmail("vu.huy@example.com");
//        user6.setVerified(true);
//        userService.save(user6);
//
//        User user7 = new User();
//        user7.setLastName("Ngô");
//        user7.setMiddleName("Bá");
//        user7.setFirstName("Dũng");
//        user7.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user7.setGender(1); // Giả sử 1 là nam
//        user7.setPhone("0987654323");
//        user7.setUsername("ngo_dung");
//        user7.setPassword("ngo_dung0987654323"); // Mật khẩu dựa trên tên và số điện thoại
//        user7.setEmail("ngo.dung@example.com");
//        user7.setVerified(true);
//        userService.save(user7);
//
//        User user8 = new User();
//        user8.setLastName("Đinh");
//        user8.setMiddleName("Thị");
//        user8.setFirstName("Thảo");
//        user8.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user8.setGender(2); // Giả sử 2 là nữ
//        user8.setPhone("0976543212");
//        user8.setUsername("dinh_thao");
//        user8.setPassword("dinh_thao0976543212"); // Mật khẩu dựa trên tên và số điện thoại
//        user8.setEmail("dinh.thao@example.com");
//        user8.setVerified(true);
//        userService.save(user8);
//
//        User user9 = new User();
//        user9.setLastName("Lý");
//        user9.setMiddleName("Minh");
//        user9.setFirstName("Tuấn");
//        user9.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user9.setGender(1); // Giả sử 1 là nam
//        user9.setPhone("0965432111");
//        user9.setUsername("ly_tuan");
//        user9.setPassword("ly_tuan0965432111"); // Mật khẩu dựa trên tên và số điện thoại
//        user9.setEmail("ly.tuan@example.com");
//        user9.setVerified(true);
//        userService.save(user9);
//
//        User user10 = new User();
//        user10.setLastName("Hoàng");
//        user10.setMiddleName("Thành");
//        user10.setFirstName("Hà");
//        user10.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user10.setGender(2); // Giả sử 2 là nữ
//        user10.setPhone("0987654324");
//        user10.setUsername("hoang_ha");
//        user10.setPassword("hoang_ha0987654324"); // Mật khẩu dựa trên tên và số điện thoại
//        user10.setEmail("hoang.ha@example.com");
//        user10.setVerified(true);
//        userService.save(user10);
//
//        User user11 = new User();
//        user11.setLastName("Võ");
//        user11.setMiddleName("Thị");
//        user11.setFirstName("Hương");
//        user11.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user11.setGender(2); // Giả sử 2 là nữ
//        user11.setPhone("0976543213");
//        user11.setUsername("vo_huong");
//        user11.setPassword("vo_huong0976543213"); // Mật khẩu dựa trên tên và số điện thoại
//        user11.setEmail("vo.huong@example.com");
//        user11.setVerified(true);
//        userService.save(user11);
//
//        User user12 = new User();
//        user12.setLastName("Đặng");
//        user12.setMiddleName("Minh");
//        user12.setFirstName("Khánh");
//        user12.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user12.setGender(1); // Giả sử 1 là nam
//        user12.setPhone("0965432112");
//        user12.setUsername("dang_khanh");
//        user12.setPassword("dang_khanh0965432112"); // Mật khẩu dựa trên tên và số điện thoại
//        user12.setEmail("dang.khanh@example.com");
//        user12.setVerified(true);
//        userService.save(user12);
//
//        User user13 = new User();
//        user13.setLastName("Nguyễn");
//        user13.setMiddleName("Thị");
//        user13.setFirstName("Hương");
//        user13.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user13.setGender(2); // Giả sử 2 là nữ
//        user13.setPhone("0987654325");
//        user13.setUsername("nguyen_huong");
//        user13.setPassword("nguyen_huong0987654325"); // Mật khẩu dựa trên tên và số điện thoại
//        user13.setEmail("nguyen.huong@example.com");
//        user13.setVerified(true);
//        userService.save(user13);
//
//        User user14 = new User();
//        user14.setLastName("Trần");
//        user14.setMiddleName("Minh");
//        user14.setFirstName("Hải");
//        user14.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user14.setGender(1); // Giả sử 1 là nam
//        user14.setPhone("0976543214");
//        user14.setUsername("tran_hai");
//        user14.setPassword("tran_hai0976543214"); // Mật khẩu dựa trên tên và số điện thoại
//        user14.setEmail("tran.hai@example.com");
//        user14.setVerified(true);
//        userService.save(user14);
//
//        User user15 = new User();
//        user15.setLastName("Phạm");
//        user15.setMiddleName("Thị");
//        user15.setFirstName("Loan");
//        user15.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user15.setGender(2); // Giả sử 2 là nữ
//        user15.setPhone("0965432113");
//        user15.setUsername("pham_loan");
//        user15.setPassword("pham_loan0965432113"); // Mật khẩu dựa trên tên và số điện thoại
//        user15.setEmail("pham.loan@example.com");
//        user15.setVerified(true);
//        userService.save(user15);
//
//        User user16 = new User();
//        user16.setLastName("Hồ");
//        user16.setMiddleName("Thị");
//        user16.setFirstName("Thảo");
//        user16.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user16.setGender(2); // Giả sử 2 là nữ
//        user16.setPhone("0987654326");
//        user16.setUsername("ho_thao");
//        user16.setPassword("ho_thao0987654326"); // Mật khẩu dựa trên tên và số điện thoại
//        user16.setEmail("ho.thao@example.com");
//        user16.setVerified(true);
//        userService.save(user16);
//
//        User user17 = new User();
//        user17.setLastName("Lê");
//        user17.setMiddleName("Minh");
//        user17.setFirstName("Tuấn");
//        user17.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user17.setGender(1); // Giả sử 1 là nam
//        user17.setPhone("0976543215");
//        user17.setUsername("le_tuan");
//        user17.setPassword("le_tuan0976543215"); // Mật khẩu dựa trên tên và số điện thoại
//        user17.setEmail("le.tuan@example.com");
//        user17.setVerified(true);
//        userService.save(user17);
//
//        User user18 = new User();
//        user18.setLastName("Vũ");
//        user18.setMiddleName("Thị");
//        user18.setFirstName("Hà");
//        user18.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user18.setGender(2); // Giả sử 2 là nữ
//        user18.setPhone("0965432114");
//        user18.setUsername("vu_ha");
//        user18.setPassword("vu_ha0965432114"); // Mật khẩu dựa trên tên và số điện thoại
//        user18.setEmail("vu.ha@example.com");
//        user18.setVerified(true);
//        userService.save(user18);
//
//        User user19 = new User();
//        user19.setLastName("Trần");
//        user19.setMiddleName("Thị");
//        user19.setFirstName("Hằng");
//        user19.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user19.setGender(2); // Giả sử 2 là nữ
//        user19.setPhone("0987654327");
//        user19.setUsername("tran_hang2");
//        user19.setPassword("tran_hang20987654327"); // Mật khẩu dựa trên tên và số điện thoại
//        user19.setEmail("tran.hang2@example.com");
//        user19.setVerified(true);
//        userService.save(user19);
//
//        User user20 = new User();
//        user20.setLastName("Lê");
//        user20.setMiddleName("Quốc");
//        user20.setFirstName("Tú");
//        user20.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user20.setGender(1); // Giả sử 1 là nam
//        user20.setPhone("0976543216");
//        user20.setUsername("le_tu2");
//        user20.setPassword("le_tu20976543216"); // Mật khẩu dựa trên tên và số điện thoại
//        user20.setEmail("le.tu2@example.com");
//        user20.setVerified(true);
//        userService.save(user20);
//
//        User user20 = new User();
//        user20.setLastName("Nguyễn");
//        user20.setMiddleName("Văn");
//        user20.setFirstName("Thuận");
//        user20.setDateOfBirth(new Timestamp(System.currentTimeMillis()));
//        user20.setGender(1); // Giả sử 1 là nam
//        user20.setPhone("0976543214");
//        user20.setUsername("vanthuan2004");
//        user20.setPassword("vanthuan2004"); // Mật khẩu dựa trên tên và số điện thoại
//        user20.setEmail("vanthuan2004@gmail.com");
//        user20.setVerified(true);
//        userService.save(user20);
    }
}
