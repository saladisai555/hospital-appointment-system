package com.example.hospital_appointment_system.repository;

import com.example.hospital_appointment_system.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {
}