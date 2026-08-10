package com.example.hospital_appointment_system.dto.request;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AppointmentBookingRequest {

    @NotNull(message = "doctorId is required")
    private Integer doctorId;

    @NotNull(message = "appointmentDate is required")
    @FutureOrPresent(message = "appointmentDate must be today or in the future")
    private LocalDate appointmentDate;

    @NotNull(message = "startTime is required")
    private LocalTime startTime;

    @Size(max = 500)
    private String reason;

    // endTime is NOT accepted from the client - the service derives it from the
    // doctor's slotDurationMinutes, so a patient can't submit a made-up duration.
}
