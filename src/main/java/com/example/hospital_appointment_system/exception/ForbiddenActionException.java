package com.example.hospital_appointment_system.exception;
public class ForbiddenActionException extends RuntimeException {
    public ForbiddenActionException(String message) { super(message); }
}