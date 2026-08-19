package com.example.hospital_appointment_system.service;

import com.example.hospital_appointment_system.dto.request.AppointmentBookingRequest;
import com.example.hospital_appointment_system.dto.request.AppointmentStatusUpdateRequest;
import com.example.hospital_appointment_system.dto.response.AppointmentResponse;

import java.util.List;

public interface AppointmentService {
    AppointmentResponse book(Integer patientUserId, AppointmentBookingRequest request);
    List<AppointmentResponse> getMyAppointments(Integer patientUserId);
    List<AppointmentResponse> getDoctorAppointments(Integer doctorUserId);
    AppointmentResponse cancelByPatient(Integer patientUserId, Integer appointmentId);
    AppointmentResponse updateStatusByDoctor(Integer doctorUserId, Integer appointmentId, AppointmentStatusUpdateRequest request);
    List<AppointmentResponse> getAll();
}