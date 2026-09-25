package com.example.hospital_appointment_system.service.impl;

import com.example.hospital_appointment_system.entity.Appointment;
import com.example.hospital_appointment_system.entity.EmailNotification;
import com.example.hospital_appointment_system.entity.NotificationType;
import com.example.hospital_appointment_system.repository.EmailNotificationRepository;
import com.example.hospital_appointment_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EmailNotificationRepository emailNotificationRepository;

    @Override
    public void createNotification(
            Appointment appointment,
            NotificationType type
    ) {

        EmailNotification notification =
                new EmailNotification();

        notification.setAppointment(appointment);

        notification.setRecipientEmail(
                appointment
                        .getPatient()
                        .getUser()
                        .getEmail()
        );

        notification.setType(type);

        emailNotificationRepository.save(notification);
    }
}