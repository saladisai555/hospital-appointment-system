package com.example.hospital_appointment_system.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardResponse {
    private long totalPatients;
    private long totalDoctors;
    private long totalDepartments;
    private long totalAppointments;
    private long bookedAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
}