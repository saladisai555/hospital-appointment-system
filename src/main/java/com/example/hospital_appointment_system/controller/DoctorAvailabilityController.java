package com.example.hospital_appointment_system.controller;

import com.example.hospital_appointment_system.dto.request.DoctorAvailabilityRequest;
import com.example.hospital_appointment_system.dto.response.DoctorAvailabilityResponse;
import com.example.hospital_appointment_system.service.AvailabilityService;
import com.example.hospital_appointment_system.service.DoctorService;
import com.example.hospital_appointment_system.util.SecurityUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor/availability")
@RequiredArgsConstructor
@Tag(name = "Doctor Availability")
@SecurityRequirement(name = "bearerAuth")
public class DoctorAvailabilityController {

    private final AvailabilityService availabilityService;
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<DoctorAvailabilityResponse> create(@Valid @RequestBody DoctorAvailabilityRequest request) {
        Integer doctorId = doctorService.getDoctorIdByUserId(SecurityUtils.currentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(availabilityService.create(doctorId, request));
    }

    @PutMapping("/{id}")
    public DoctorAvailabilityResponse update(@PathVariable Integer id,
                                             @Valid @RequestBody DoctorAvailabilityRequest request) {
        Integer doctorId = doctorService.getDoctorIdByUserId(SecurityUtils.currentUserId());
        return availabilityService.update(doctorId, id, request);
    }
    @GetMapping
    public List<DoctorAvailabilityResponse> getMyAvailability() {
        Integer doctorId = doctorService.getDoctorIdByUserId(SecurityUtils.currentUserId());
        return availabilityService.getByDoctor(doctorId);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Integer doctorId = doctorService.getDoctorIdByUserId(SecurityUtils.currentUserId());
        availabilityService.delete(doctorId, id);
        return ResponseEntity.noContent().build();
    }
}