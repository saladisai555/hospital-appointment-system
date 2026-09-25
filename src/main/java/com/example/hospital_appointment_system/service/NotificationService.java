package com.example.hospital_appointment_system.service;

import com.example.hospital_appointment_system.entity.Appointment;
import com.example.hospital_appointment_system.entity.NotificationType;

public interface NotificationService {

    void createNotification(
            Appointment appointment,
            NotificationType type
    );
}