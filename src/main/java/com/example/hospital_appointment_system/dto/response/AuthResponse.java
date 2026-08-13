package com.example.hospital_appointment_system.dto.response;
import com.example.hospital_appointment_system.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Integer userId;
    private String name;
    private String email;
    private Role role;
}