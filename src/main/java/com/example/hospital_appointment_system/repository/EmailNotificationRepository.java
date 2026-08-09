package com.example.hospital_appointment_system.repository;
import com.example.hospital_appointment_system.entity.EmailNotification;
import com.example.hospital_appointment_system.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailNotificationRepository extends JpaRepository<EmailNotification, Integer> {

    List<EmailNotification> findByStatus(NotificationStatus status);

    List<EmailNotification> findByAppointmentId(Integer appointmentId);
}