package com.example.hospital_appointment_system.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DoctorUpdateRequest {

    private Integer departmentId;

    @Size(max = 150)
    private String specialization;

    @Min(0)
    private Integer experienceYears;

    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal consultationFee;

    @Size(max = 1000)
    private String bio;

    private Boolean active;
}