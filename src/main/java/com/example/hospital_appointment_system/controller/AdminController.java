package com.example.hospital_appointment_system.controller;

import com.example.hospital_appointment_system.dto.request.DepartmentRequest;
import com.example.hospital_appointment_system.dto.request.DoctorCreateRequest;
import com.example.hospital_appointment_system.dto.request.DoctorUpdateRequest;
import com.example.hospital_appointment_system.dto.response.AdminDashboardResponse;
import com.example.hospital_appointment_system.dto.response.AppointmentResponse;
import com.example.hospital_appointment_system.dto.response.DepartmentResponse;
import com.example.hospital_appointment_system.dto.response.DoctorResponse;
import com.example.hospital_appointment_system.service.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin")
public class AdminController {

    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final AppointmentService appointmentService;
    private final AdminService adminService;

    // ---- Doctors ----
    @PostMapping("/doctors")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody DoctorCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(request));
    }

    @PutMapping("/doctors/{id}")
    public DoctorResponse updateDoctor(@PathVariable Integer id, @Valid @RequestBody DoctorUpdateRequest request) {
        return doctorService.update(id, request);
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Integer id) {
        doctorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Departments ----
    @PostMapping("/departments")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(request));
    }

    @PutMapping("/departments/{id}")
    public DepartmentResponse updateDepartment(@PathVariable Integer id, @Valid @RequestBody DepartmentRequest request) {
        return departmentService.update(id, request);
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Integer id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Appointments (view all) ----
    @GetMapping("/appointments")
    public List<AppointmentResponse> allAppointments() {
        return appointmentService.getAll();
    }

    // ---- Dashboard ----
    @GetMapping("/dashboard")
    public AdminDashboardResponse dashboard() {
        return adminService.getDashboard();
    }
}