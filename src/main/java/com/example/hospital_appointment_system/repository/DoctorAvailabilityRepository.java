package com.example.hospital_appointment_system.repository;

import com.example.hospital_appointment_system.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public interface DoctorAvailabilityRepository
        extends JpaRepository<DoctorAvailability, Integer> {

    List<DoctorAvailability> findByDoctorIdAndActiveTrue(
            Integer doctorId
    );

    List<DoctorAvailability> findByDoctorIdAndDayOfWeekAndActiveTrue(
            Integer doctorId,
            DayOfWeek dayOfWeek
    );

    @Query(value = """
        SELECT COUNT(*)
        FROM doctor_availability
        WHERE doctor_id = :doctorId
          AND day_of_week = :dayOfWeek
          AND active = 1
          AND id <> COALESCE(:excludeId, -1)
          AND start_time < CAST(:endTime AS time)
          AND end_time > CAST(:startTime AS time)
        """, nativeQuery = true)
    int existsOverlapping(
            @Param("doctorId") Integer doctorId,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Integer excludeId
    );
}