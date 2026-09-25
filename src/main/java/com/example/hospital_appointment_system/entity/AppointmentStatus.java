package com.example.hospital_appointment_system.entity;
public enum AppointmentStatus {

    BOOKED,
    CONFIRMED,
    CANCELLED,
    COMPLETED,
    REJECTED,
    NO_SHOW;

    public boolean isFinal() {
        return this == CANCELLED
                || this == COMPLETED
                || this == REJECTED
                || this == NO_SHOW;
    }
}
