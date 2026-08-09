package com.example.hospital_appointment_system.repository;

import com.example.hospital_appointment_system.entity.Appointment;
import com.example.hospital_appointment_system.entity.AppointmentStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    List<Appointment> findByPatientIdOrderByAppointmentDateDescStartTimeDesc(Integer patientId);

    List<Appointment> findByDoctorIdOrderByAppointmentDateDescStartTimeDesc(Integer doctorId);

    List<Appointment> findByDoctorIdAndAppointmentDate(Integer doctorId, LocalDate appointmentDate);

    // Core conflict-prevention query for Phase 8: is there any ACTIVE appointment
    // for this doctor, on this date, whose time range overlaps the requested range?
    // "Active" = not CANCELLED and not REJECTED (those free up the slot).
    @Query("""
            SELECT COUNT(a) > 0 FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.appointmentDate = :date
              AND a.status NOT IN (com.example.hospital.entity.AppointmentStatus.CANCELLED,
                                    com.example.hospital.entity.AppointmentStatus.REJECTED)
              AND a.startTime < :endTime
              AND a.endTime > :startTime
            """)
    boolean existsOverlappingAppointment(@Param("doctorId") Integer doctorId,
                                         @Param("date") LocalDate date,
                                         @Param("startTime") LocalTime startTime,
                                         @Param("endTime") LocalTime endTime);

    // Row-level pessimistic lock on the doctor's appointments for that date, taken
    // BEFORE the overlap check in Phase 8's booking transaction, so two concurrent
    // requests for the same slot can't both pass the check and both insert.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a FROM Appointment a
            WHERE a.doctor.id = :doctorId
              AND a.appointmentDate = :date
            """)
    List<Appointment> lockAppointmentsForDoctorAndDate(@Param("doctorId") Integer doctorId,
                                                       @Param("date") LocalDate date);

    boolean existsByPatientIdAndDoctorIdAndAppointmentDateAndStartTimeAndStatusNotIn(
            Integer patientId, Integer doctorId, LocalDate appointmentDate, LocalTime startTime,
            List<AppointmentStatus> excludedStatuses);
}