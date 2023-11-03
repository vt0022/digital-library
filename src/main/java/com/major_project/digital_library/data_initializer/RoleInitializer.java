package com.major_project.digital_library.data_initializer;

import com.major_project.digital_library.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class RoleInitializer implements CommandLineRunner {
    @Autowired
    private IRoleService roleService;

    @Override
    public void run(String... args) throws Exception {
//        Role r1 = new Role();
//        r1.setRoleName("ROLE_STUDENT");
//        roleService.save(r1);
//
//        Role r2 = new Role();
//        r2.setRoleName("ROLE_LECTURER");
//        roleService.save(r2);
//
//        Role r3 = new Role();
//        r3.setRoleName("ROLE_MANAGER");
//        roleService.save(r3);
//
//        Role r4 = new Role();
//        r4.setRoleName("ROLE_ADMIN");
//        roleService.save(r4);
    }
}
