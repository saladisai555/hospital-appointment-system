package com.example.hospital_appointment_system.controller;

import com.example.hospital_appointment_system.dto.request.LoginRequest;
import com.example.hospital_appointment_system.dto.request.RegisterRequest;
import com.example.hospital_appointment_system.dto.response.AuthResponse;
import com.example.hospital_appointment_system.dto.response.PatientResponse;
import com.example.hospital_appointment_system.service.AuthService;
import com.example.hospital_appointment_system.service.PatientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;
    private final PatientService patientService;

    @PostMapping("/register")
    public ResponseEntity<PatientResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}