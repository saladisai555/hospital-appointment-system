package com.example.hospital_appointment_system.repository;

import com.example.hospital_appointment_system.entity.Appointment;
import com.example.hospital_appointment_system.entity.AppointmentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.QueryHints;
import jakarta.persistence.QueryHint;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDescStartTimeDesc(
            Integer patientId
    );

    List<Appointment> findByDoctorIdOrderByAppointmentDateDescStartTimeDesc(
            Integer doctorId
    );

    boolean existsByDoctorIdAndAppointmentDateAndStatusNotInAndStartTimeLessThanAndEndTimeGreaterThan(
            Integer doctorId,
            LocalDate appointmentDate,
            List<AppointmentStatus> excludedStatuses,
            LocalTime endTime,
            LocalTime startTime
    );

    boolean existsByPatientIdAndDoctorIdAndAppointmentDateAndStartTimeAndStatusNotIn(
            Integer patientId,
            Integer doctorId,
            LocalDate appointmentDate,
            LocalTime startTime,
            List<AppointmentStatus> excludedStatuses
    );

    long countByStatus(AppointmentStatus status);
}