package com.example.hospital_appointment_system.controller;

import com.example.hospital_appointment_system.dto.response.DoctorAvailabilityResponse;
import com.example.hospital_appointment_system.dto.response.DoctorResponse;
import com.example.hospital_appointment_system.service.AvailabilityService;
import com.example.hospital_appointment_system.service.DoctorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors")
public class DoctorController {

    private final DoctorService doctorService;
    private final AvailabilityService availabilityService;

    @GetMapping
    public Page<DoctorResponse> search(@RequestParam(required = false) Integer departmentId,
                                       @RequestParam(required = false) String specialization,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        return doctorService.search(departmentId, specialization, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public DoctorResponse getById(@PathVariable Integer id) {
        return doctorService.getById(id);
    }

    @GetMapping("/{id}/availability")
    public List<DoctorAvailabilityResponse> getAvailability(@PathVariable Integer id,
                                                            @RequestParam(required = false) LocalDate date) {
        return date != null
                ? availabilityService.getByDoctorAndDate(id, date)
                : availabilityService.getByDoctor(id);
    }
}