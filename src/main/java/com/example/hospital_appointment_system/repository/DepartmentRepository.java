package com.example.hospital_appointment_system.repository;

import com.example.hospital_appointment_system.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Optional<Department> findByName(String name);

    boolean existsByName(String name);
}
