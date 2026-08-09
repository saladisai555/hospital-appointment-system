package com.example.hospital_appointment_system.repository;

import com.example.hospital_appointment_system.entity.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.DayOfWeek;
import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Integer> {

    List<DoctorAvailability> findByDoctorIdAndActiveTrue(Integer doctorId);

    List<DoctorAvailability> findByDoctorIdAndDayOfWeekAndActiveTrue(Integer doctorId, DayOfWeek dayOfWeek);

    // Used in Phase 8 to reject an availability rule that overlaps an existing one
    // for the same doctor/day (e.g. doctor tries to add 09:00-13:00 when 10:00-11:00 already exists).
    @Query("""
            SELECT COUNT(a) > 0 FROM DoctorAvailability a
            WHERE a.doctor.id = :doctorId
              AND a.dayOfWeek = :dayOfWeek
              AND a.active = true
              AND a.id <> COALESCE(:excludeId, -1)
              AND a.startTime < :endTime
              AND a.endTime > :startTime
            """)
    boolean existsOverlapping(@Param("doctorId") Integer doctorId,
                              @Param("dayOfWeek") DayOfWeek dayOfWeek,
                              @Param("startTime") java.time.LocalTime startTime,
                              @Param("endTime") java.time.LocalTime endTime,
                              @Param("excludeId") Integer excludeId);
}
