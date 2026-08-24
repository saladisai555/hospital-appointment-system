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

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public PatientResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("An account with this email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.PATIENT);
        user.setPhone(request.getPhone());
        user.setActive(true);
        userRepository.save(user);

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
