package com.example.hospital_appointment_system.controller;

import com.example.hospital_appointment_system.dto.request.AppointmentBookingRequest;
import com.example.hospital_appointment_system.dto.response.AppointmentResponse;
import com.example.hospital_appointment_system.security.CurrentUserService;
import com.example.hospital_appointment_system.service.AppointmentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Patient booking, viewing, and cancelling appointments")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final CurrentUserService currentUserService;
    @PostMapping
    public ResponseEntity<AppointmentResponse> book(@Valid @RequestBody AppointmentBookingRequest request) {
        AppointmentResponse response = appointmentService.book(
                currentUserService.getCurrentUserId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public List<AppointmentResponse> myAppointments() {
        return appointmentService.getMyAppointments(currentUserService.getCurrentUserId());
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable Integer id) {
        return appointmentService.cancelByPatient(currentUserService.getCurrentUserId(), id);
    }
}