package com.example.hospital_appointment_system.dto.request;
import com.example.hospital_appointment_system.entity.AppointmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentStatusUpdateRequest {

    @NotNull(message = "status is required")
    private AppointmentStatus status;

    @jakarta.validation.constraints.Size(max = 1000)
    private String notes;
}