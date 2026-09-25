package com.example.hospital_appointment_system.service.impl;

import com.example.hospital_appointment_system.dto.response.AdminDashboardResponse;
import com.example.hospital_appointment_system.entity.AppointmentStatus;
import com.example.hospital_appointment_system.entity.Role;
import com.example.hospital_appointment_system.repository.*;
import com.example.hospital_appointment_system.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;
    @Override
    @Transactional(readOnly = true)
    public AdminDashboardResponse getDashboard() {

        long totalAppointments =
                appointmentRepository.count();

        long bookedAppointments =
                appointmentRepository.countByStatus(
                        AppointmentStatus.BOOKED
                );

        long completedAppointments =
                appointmentRepository.countByStatus(
                        AppointmentStatus.COMPLETED
                );

        long cancelledAppointments =
                appointmentRepository.countByStatus(
                        AppointmentStatus.CANCELLED
                );

        return AdminDashboardResponse.builder()
                .totalPatients(
                        userRepository.countByRole(Role.PATIENT)
                )
                .totalDoctors(
                        doctorRepository.count()
                )
                .totalDepartments(
                        departmentRepository.count()
                )
                .totalAppointments(totalAppointments)
                .bookedAppointments(bookedAppointments)
                .completedAppointments(completedAppointments)
                .cancelledAppointments(cancelledAppointments)
                .build();
    }
}