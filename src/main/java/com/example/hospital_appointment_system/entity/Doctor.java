package com.example.hospital_appointment_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "doctors")
@Getter
@Setter
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(length = 150)
    private String specialization;

    @Column(name = "license_number", nullable = false, unique = true, length = 50)
    private String licenseNumber;

    @Column(name = "experience_years", nullable = false)
    private int experienceYears;

    @Column(name = "consultation_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal consultationFee;

    @Column(length = 1000)
    private String bio;

    @Column(nullable = false)
    private boolean active = true;

    @Override
    public String toString() {
        return "Doctor{id=" + id + ", specialization='" + specialization + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor doctor)) return false;
        return id != null && id.equals(doctor.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
