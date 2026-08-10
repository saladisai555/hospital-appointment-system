package com.example.hospital_appointment_system.dto.request;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// Used by ADMIN to create a doctor - creates both the User (role=DOCTOR) and Doctor profile.
@Getter
@Setter
public class DoctorCreateRequest {

    @NotBlank
    @Size(max = 150)
    private String name;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    private String phone;

    @NotNull(message = "departmentId is required")
    private Integer departmentId;

    @Size(max = 150)
    private String specialization;

    @NotBlank(message = "License number is required")
    @Size(max = 50)
    private String licenseNumber;

    @Min(0)
    private int experienceYears;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal consultationFee;

    @Size(max = 1000)
    private String bio;
}