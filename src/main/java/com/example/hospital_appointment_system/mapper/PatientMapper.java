package com.example.hospital_appointment_system.mapper;

import com.example.hospital_appointment_system.dto.response.PatientResponse;
import com.example.hospital_appointment_system.entity.Patient;

public class PatientMapper {

    private PatientMapper() {}

    public static PatientResponse toResponse(Patient patient) {
        if (patient == null) return null;
        return PatientResponse.builder()
                .id(patient.getId())
                .name(patient.getUser().getName())
                .email(patient.getUser().getEmail())
                .phone(patient.getUser().getPhone())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .address(patient.getAddress())
                .build();
    }
}
