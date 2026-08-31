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
        var all = appointmentRepository.findAll();
        return AdminDashboardResponse.builder()
                .totalPatients(userRepository.countByRole(Role.PATIENT))
                .totalDoctors(doctorRepository.count())
                .totalDepartments(departmentRepository.count())
                .totalAppointments(all.size())
                .bookedAppointments(all.stream().filter(a -> a.getStatus() == AppointmentStatus.BOOKED).count())
                .completedAppointments(all.stream().filter(a -> a.getStatus() == AppointmentStatus.COMPLETED).count())
                .cancelledAppointments(all.stream().filter(a -> a.getStatus() == AppointmentStatus.CANCELLED).count())
                .build();
    }
}