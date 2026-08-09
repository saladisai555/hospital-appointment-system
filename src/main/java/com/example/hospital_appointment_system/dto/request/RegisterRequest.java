package com.example.hospital_appointment_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String phone;

    // Only patient self-registration goes through this endpoint (per spec:
    // "PATIENT registration creates a patient account"). Doctors/admins are
    // created by ADMIN via /api/admin/doctors, not here.
    private String dateOfBirth; // ISO string, parsed to LocalDate in the mapper/service
    private String gender;
    private String address;
}
