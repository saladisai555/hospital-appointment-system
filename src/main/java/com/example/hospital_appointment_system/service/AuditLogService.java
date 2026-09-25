package com.example.hospital_appointment_system.service;

import com.example.hospital_appointment_system.entity.User;

public interface AuditLogService {

    void log(
            User user,
            String action,
            String entityType,
            Integer entityId,
            String details
    );
}