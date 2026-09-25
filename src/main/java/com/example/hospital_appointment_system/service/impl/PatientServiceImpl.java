package com.example.hospital_appointment_system.service.impl;
import com.example.hospital_appointment_system.dto.request.RegisterRequest;
import com.example.hospital_appointment_system.dto.response.PatientResponse;
import com.example.hospital_appointment_system.entity.Patient;
import com.example.hospital_appointment_system.entity.Role;
import com.example.hospital_appointment_system.entity.User;
import com.example.hospital_appointment_system.exception.ConflictException;
import com.example.hospital_appointment_system.exception.ResourceNotFoundException;
import com.example.hospital_appointment_system.mapper.PatientMapper;
import com.example.hospital_appointment_system.repository.PatientRepository;
import com.example.hospital_appointment_system.repository.UserRepository;
import com.example.hospital_appointment_system.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import com.example.hospital_appointment_system.entity.Role;
import com.example.hospital_appointment_system.service.UserAccountService;
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final UserAccountService userAccountService;

    @Override
    @Transactional
    public PatientResponse register(RegisterRequest request) {
        User user = userAccountService.createAccount(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                Role.PATIENT,
                request.getPhone()
        );

        Patient patient = new Patient();
        patient.setUser(user);
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isBlank()) {
            patient.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
        }
        patient.setGender(request.getGender());
        patient.setAddress(request.getAddress());
        patientRepository.save(patient);

        return PatientMapper.toResponse(patient);
    }

    @Override
    @Transactional(readOnly = true)
    public PatientResponse getByUserId(Integer userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        return PatientMapper.toResponse(patient);
    }
}
