package com.example.hospital_appointment_system.mapper;

import com.example.hospital_appointment_system.dto.response.DoctorResponse;
import com.example.hospital_appointment_system.entity.Doctor;

public class DoctorMapper {

    private DoctorMapper() {}

    public static DoctorResponse toResponse(Doctor doctor) {
        if (doctor == null) return null;
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getUser().getName())
                .email(doctor.getUser().getEmail())
                .phone(doctor.getUser().getPhone())
                .department(DepartmentMapper.toResponse(doctor.getDepartment()))
                .specialization(doctor.getSpecialization())
                .licenseNumber(doctor.getLicenseNumber())
                .experienceYears(doctor.getExperienceYears())
                .consultationFee(doctor.getConsultationFee())
                .bio(doctor.getBio())
                .active(doctor.isActive())
                .build();
    }
}
