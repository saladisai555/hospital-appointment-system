package com.example.hospital_appointment_system.service;

import com.example.hospital_appointment_system.dto.request.DoctorAvailabilityRequest;
import com.example.hospital_appointment_system.dto.response.DoctorAvailabilityResponse;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {
    List<DoctorAvailabilityResponse> getByDoctor(Integer doctorId);
    List<DoctorAvailabilityResponse> getByDoctorAndDate(Integer doctorId, LocalDate date);
    DoctorAvailabilityResponse create(Integer doctorId, DoctorAvailabilityRequest request);
    DoctorAvailabilityResponse update(Integer doctorId, Integer availabilityId, DoctorAvailabilityRequest request);
    void delete(Integer doctorId, Integer availabilityId);
}