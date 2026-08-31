package com.example.hospital_appointment_system.controller;

import com.example.hospital_appointment_system.dto.request.AppointmentStatusUpdateRequest;
import com.example.hospital_appointment_system.dto.response.AppointmentResponse;
import com.example.hospital_appointment_system.service.AppointmentService;
import com.example.hospital_appointment_system.util.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor/appointments")
@RequiredArgsConstructor
@Tag(name = "Doctor Appointments")
@SecurityRequirement(name = "bearerAuth")
public class DoctorAppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    public List<AppointmentResponse> myAppointments() {
        return appointmentService.getDoctorAppointments(SecurityUtils.currentUserId());
    }

    @PatchMapping("/{id}/status")
    public AppointmentResponse updateStatus(@PathVariable Integer id,
                                            @Valid @RequestBody AppointmentStatusUpdateRequest request) {
        return appointmentService.updateStatusByDoctor(SecurityUtils.currentUserId(), id, request);
    }
}