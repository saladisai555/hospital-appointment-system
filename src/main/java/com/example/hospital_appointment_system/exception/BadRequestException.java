package com.example.hospital_appointment_system.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) { super(message); }
}