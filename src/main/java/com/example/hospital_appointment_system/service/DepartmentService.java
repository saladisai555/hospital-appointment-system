package com.example.hospital_appointment_system.service;

import com.example.hospital_appointment_system.dto.request.DepartmentRequest;
import com.example.hospital_appointment_system.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAll();
    DepartmentResponse getById(Integer id);
    DepartmentResponse create(DepartmentRequest request);
    DepartmentResponse update(Integer id, DepartmentRequest request);
    void delete(Integer id);
}