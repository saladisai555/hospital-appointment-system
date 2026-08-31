package com.example.hospital_appointment_system.service;

import com.example.hospital_appointment_system.dto.request.DoctorCreateRequest;
import com.example.hospital_appointment_system.dto.request.DoctorUpdateRequest;
import com.example.hospital_appointment_system.dto.response.DoctorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {
    Page<DoctorResponse> search(Integer departmentId, String specialization, Pageable pageable);
    DoctorResponse getById(Integer id);
    DoctorResponse create(DoctorCreateRequest request);
    DoctorResponse update(Integer id, DoctorUpdateRequest request);
    void delete(Integer id);
    Integer getDoctorIdByUserId(Integer userId);
}