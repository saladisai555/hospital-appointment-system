package com.example.hospital_appointment_system.repository;

import com.example.hospital_appointment_system.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Integer> {

    Optional<Doctor> findByUserId(Integer userId);

    boolean existsByLicenseNumber(String licenseNumber);

    // Backs GET /api/doctors?departmentId=&specialization=&page=&size=
    // Either filter can be null - COALESCE/ISNULL-style optional matching via JPQL.
    @Query("""
            SELECT d FROM Doctor d
            WHERE d.active = true
              AND (:departmentId IS NULL OR d.department.id = :departmentId)
              AND (:specialization IS NULL OR LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%')))
            """)
    Page<Doctor> search(@Param("departmentId") Integer departmentId,
                        @Param("specialization") String specialization,
                        Pageable pageable);
}