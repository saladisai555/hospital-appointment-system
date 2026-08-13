package com.example.hospital_appointment_system.dto.response;
import com.example.hospital_appointment_system.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Integer id;
    private String name;
    private String email;
    private Role role;
    private String phone;
    private boolean active;
}
