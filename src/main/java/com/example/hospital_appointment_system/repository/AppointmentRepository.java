package com.example.hospital_appointment_system.repository;

import com.example.hospital_appointment_system.entity.Appointment;
import com.example.hospital_appointment_system.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDescStartTimeDesc(
            Integer patientId
    );

    List<Appointment> findByDoctorIdOrderByAppointmentDateDescStartTimeDesc(
            Integer doctorId
    );

    @Query(value = """
        SELECT COUNT(*)
        FROM appointments a
        WHERE a.doctor_id = :doctorId
          AND a.appointment_date = :appointmentDate
          AND a.status NOT IN (:excludedStatuses)
          AND a.start_time < CAST(:endTime AS time)
          AND a.end_time > CAST(:startTime AS time)
        """,
            nativeQuery = true)
    long countOverlappingAppointments(
            @Param("doctorId") Integer doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("excludedStatuses") List<String> excludedStatuses,
            @Param("endTime") LocalTime endTime,
            @Param("startTime") LocalTime startTime
    );

    @Query(value = """
    SELECT COUNT(*)
    FROM appointments a
    WHERE a.patient_id = :patientId
      AND a.doctor_id = :doctorId
      AND a.appointment_date = :appointmentDate
      AND a.start_time = CAST(:startTime AS time)
      AND a.status NOT IN (:excludedStatuses)
    """,
            nativeQuery = true)
    long countDuplicateBookings(
            @Param("patientId") Integer patientId,
            @Param("doctorId") Integer doctorId,
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("startTime") LocalTime startTime,
            @Param("excludedStatuses") List<String> excludedStatuses
    );

    long countByStatus(AppointmentStatus status);
}