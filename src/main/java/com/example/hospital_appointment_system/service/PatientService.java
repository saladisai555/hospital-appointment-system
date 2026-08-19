package com.example.hospital_appointment_system.service;

import com.example.hospital_appointment_system.dto.request.RegisterRequest;
import com.example.hospital_appointment_system.dto.response.PatientResponse;

public interface PatientService {
    PatientResponse register(RegisterRequest request);
    PatientResponse getByUserId(Integer userId);
}
