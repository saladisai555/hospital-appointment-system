package com.example.hospital_appointment_system.service;

import com.example.hospital_appointment_system.dto.request.LoginRequest;
import com.example.hospital_appointment_system.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
}