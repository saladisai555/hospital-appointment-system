package com.example.hospital_appointment_system.service.impl;

import com.example.hospital_appointment_system.dto.request.DoctorCreateRequest;
import com.example.hospital_appointment_system.dto.request.DoctorUpdateRequest;
import com.example.hospital_appointment_system.dto.response.DoctorResponse;
import com.example.hospital_appointment_system.entity.Department;
import com.example.hospital_appointment_system.entity.Doctor;
import com.example.hospital_appointment_system.entity.Role;
import com.example.hospital_appointment_system.entity.User;
import com.example.hospital_appointment_system.exception.ConflictException;
import com.example.hospital_appointment_system.exception.ResourceNotFoundException;
import com.example.hospital_appointment_system.mapper.DoctorMapper;
import com.example.hospital_appointment_system.repository.DepartmentRepository;
import com.example.hospital_appointment_system.repository.DoctorRepository;
import com.example.hospital_appointment_system.repository.UserRepository;
import com.example.hospital_appointment_system.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponse> search(Integer departmentId, String specialization, Pageable pageable) {
        return doctorRepository.search(departmentId, specialization, pageable)
                .map(DoctorMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getDoctorIdByUserId(Integer userId) {
        return doctorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"))
                .getId();
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorResponse getById(Integer id) {
        return DoctorMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public DoctorResponse create(DoctorCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("An account with this email already exists");
        }
        if (doctorRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new ConflictException("A doctor with this license number already exists");
        }
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + request.getDepartmentId()));

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.DOCTOR);
        user.setPhone(request.getPhone());
        user.setActive(true);
        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setDepartment(department);
        doctor.setSpecialization(request.getSpecialization());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setExperienceYears(request.getExperienceYears());
        doctor.setConsultationFee(request.getConsultationFee());
        doctor.setBio(request.getBio());
        doctor.setActive(true);

        return DoctorMapper.toResponse(doctorRepository.save(doctor));
    }

    @Override
    @Transactional
    public DoctorResponse update(Integer id, DoctorUpdateRequest request) {
        Doctor doctor = findEntity(id);

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + request.getDepartmentId()));
            doctor.setDepartment(department);
        }
        if (request.getSpecialization() != null) doctor.setSpecialization(request.getSpecialization());
        if (request.getExperienceYears() != null) doctor.setExperienceYears(request.getExperienceYears());
        if (request.getConsultationFee() != null) doctor.setConsultationFee(request.getConsultationFee());
        if (request.getBio() != null) doctor.setBio(request.getBio());
        if (request.getActive() != null) doctor.setActive(request.getActive());

        return DoctorMapper.toResponse(doctor);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        findEntity(id).setActive(false);
    }

    private Doctor findEntity(Integer id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
    }
}