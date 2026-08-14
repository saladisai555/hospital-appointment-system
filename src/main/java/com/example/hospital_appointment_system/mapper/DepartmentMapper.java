package com.example.hospital_appointment_system.mapper;

import com.example.hospital_appointment_system.dto.response.DepartmentResponse;
import com.example.hospital_appointment_system.entity.Department;

public class DepartmentMapper {

    private DepartmentMapper() {}

    public static DepartmentResponse toResponse(Department department) {
        if (department == null) return null;
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .active(department.isActive())
                .build();
    }
}
