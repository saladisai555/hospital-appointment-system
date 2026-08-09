package com.example.hospital_appointment_system.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Getter
@Setter
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Owning side of User <-> Patient (one-to-one). FK lives on patients.user_id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(length = 255)
    private String address;

    @Override
    public String toString() {
        return "Patient{id=" + id + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return id != null && id.equals(patient.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}