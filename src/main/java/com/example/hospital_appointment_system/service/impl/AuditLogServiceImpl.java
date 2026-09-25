package com.example.hospital_appointment_system.service.impl;

import com.example.hospital_appointment_system.entity.AuditLog;
import com.example.hospital_appointment_system.entity.User;
import com.example.hospital_appointment_system.repository.AuditLogRepository;
import com.example.hospital_appointment_system.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(
            User user,
            String action,
            String entityType,
            Integer entityId,
            String details
    ) {

        AuditLog auditLog = new AuditLog();

        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);

        auditLogRepository.save(auditLog);
    }
}