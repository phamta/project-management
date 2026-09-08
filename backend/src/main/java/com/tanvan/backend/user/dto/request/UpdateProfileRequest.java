package com.tanvan.backend.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor 
public class UpdateProfileRequest {
    private String fullName;
    private String phone;
    private String email;
}