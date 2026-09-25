package com.example.hospital_appointment_system.repository;

import com.example.hospital_appointment_system.entity.Doctor;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    Optional<Doctor> findByUserId(Integer userId);

    boolean existsByLicenseNumber(String licenseNumber);

    @Query("""
            SELECT d FROM Doctor d
            WHERE d.active = true
              AND (:departmentId IS NULL OR d.department.id = :departmentId)
              AND (:specialization IS NULL OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%')))
            """)
    Page<Doctor> search(@Param("departmentId") Integer departmentId,
                        @Param("specialization") String specialization,
                        Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({
            @QueryHint(
                    name = "jakarta.persistence.lock.timeout",
                    value = "3000"
            )
    })
    @Query("""
        SELECT d
        FROM Doctor d
        WHERE d.id = :doctorId
        """)
    Optional<Doctor> findByIdForUpdate(
            @Param("doctorId") Integer doctorId
    );
}