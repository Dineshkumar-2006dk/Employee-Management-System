package com.ems.config;

import com.ems.dto.RegisterRequest;
import com.ems.repository.UserRepository;
import com.ems.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with a default administrator user if it is empty.
 */
@Component
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // If no user exists, seed the default admin
        if (userRepository.count() == 0) {
            RegisterRequest defaultAdmin = new RegisterRequest(
                    "admin",
                    "admin@ems.com",
                    "password123",
                    "ADMIN"
            );
            userService.registerAdmin(defaultAdmin);
            System.out.println("====== SEEDING DATABASE: Default administrator registered (admin / password123) ======");
        }
    }
}
