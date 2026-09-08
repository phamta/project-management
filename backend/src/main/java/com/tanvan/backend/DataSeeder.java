package com.tanvan.backend;

import com.tanvan.backend.auth.entity.Role;
import com.tanvan.backend.auth.entity.User;
import com.tanvan.backend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("🚀 Starting Data Seeder...");
        seedUsers();
        log.info("✅ Data Seeder completed!");
    }

    private void seedUsers() {
        // Kiểm tra nếu đã có user thì không seed lại
        if (userRepository.count() > 0) {
            log.info("📊 Users already exist. Skipping seed.");
            return;
        }

        log.info("🌱 Seeding users...");

        List<User> users = List.of(
            createUser("admin", "admin@collabflow.com", "admin123", "Quản Trị Viên", "0900000001", Role.ADMIN),
            createUser("quanlyduan", "quanly.duan@collabflow.com", "manager123", "Nguyễn Văn Quản Lý", "0900000002", Role.PROJECT_MANAGER),
            createUser("truongnhom", "truong.nhom@collabflow.com", "lead123", "Trần Thị Trưởng Nhóm", "0900000003", Role.TEAM_LEAD),
            createUser("thanhvien1", "thanh.vien1@collabflow.com", "member123", "Lê Văn Thành Viên", "0900000004", Role.MEMBER),
            createUser("thanhvien2", "thanh.vien2@collabflow.com", "member123", "Phạm Thị Thành Viên", "0900000005", Role.MEMBER)
        );

        userRepository.saveAll(users);
        log.info("✅ Created {} users successfully!", users.size());
    }

    private User createUser(String username, String email, String rawPassword, String fullName, String phone, Role role) {
        return User.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .fullName(fullName)
                .phone(phone)
                .role(role)
                .enabled(true)
                .build();
    }
}