// com/tanvan/backend/auth/entity/Role.java
package com.tanvan.backend.auth.entity;

public enum Role {
    ADMIN, 
    PROJECT_MANAGER, 
    TEAM_LEAD, 
    MEMBER,
    USER; // ← FIX: thêm role USER để tránh lỗi khi Cognito không có nhóm nào
}