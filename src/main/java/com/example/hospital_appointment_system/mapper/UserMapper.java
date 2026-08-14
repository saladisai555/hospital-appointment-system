package com.example.hospital_appointment_system.mapper;

import com.example.hospital_appointment_system.dto.response.UserResponse;
import com.example.hospital_appointment_system.entity.User;

public class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .phone(user.getPhone())
                .active(user.isActive())
                .build();
    }
}