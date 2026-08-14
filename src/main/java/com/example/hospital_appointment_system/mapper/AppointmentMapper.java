package com.example.hospital_appointment_system.mapper;

import com.example.hospital_appointment_system.dto.response.AppointmentResponse;
import com.example.hospital_appointment_system.entity.Appointment;

public class AppointmentMapper {

    private AppointmentMapper() {}

    public static AppointmentResponse toResponse(Appointment appointment) {
        if (appointment == null) return null;
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatient().getId())
                .patientName(appointment.getPatient().getUser().getName())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getUser().getName())
                .departmentName(appointment.getDoctor().getDepartment().getName())
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
