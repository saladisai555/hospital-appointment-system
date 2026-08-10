package com.example.hospital_appointment_system.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class DoctorAvailabilityRequest {

    @NotNull(message = "dayOfWeek is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "startTime is required")
    private LocalTime startTime;

    @NotNull(message = "endTime is required")
    private LocalTime endTime;

    @NotNull(message = "slotDurationMinutes is required")
    private Integer slotDurationMinutes;

    // Cross-field check (start < end) is enforced in the service layer in Phase 6/8,
    // not here - Bean Validation alone can't easily compare two fields on this DTO
    // without a custom class-level annotation, and the service needs to do a DB-aware
    // overlap check anyway, so the ordering check lives there too for one source of truth.
}