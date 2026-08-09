package com.example.hospital_appointment_system.repository;
import com.example.hospital_appointment_system.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Integer> {

    Optional<Patient> findByUserId(Integer userId);
}