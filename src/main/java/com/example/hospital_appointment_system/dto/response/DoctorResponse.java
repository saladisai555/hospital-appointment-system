package com.example.hospital_appointment_system.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DoctorResponse {
    private Integer id;
    private String name;           // from linked User
    private String email;          // from linked User
    private String phone;          // from linked User
    private DepartmentResponse department;
    private String specialization;
    private String licenseNumber;
    private int experienceYears;
    private BigDecimal consultationFee;
    private String bio;
    private boolean active;
}