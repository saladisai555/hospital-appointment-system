package com.example.hospital_appointment_system.service.booking;

import com.example.hospital_appointment_system.dto.request.AppointmentBookingRequest;
import com.example.hospital_appointment_system.entity.Doctor;
import com.example.hospital_appointment_system.entity.Patient;
import com.example.hospital_appointment_system.exception.BadRequestException;
import com.example.hospital_appointment_system.exception.ForbiddenActionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class AppointmentBookingValidator {

    public void validatePatient(Patient patient) {

        if (!patient.getUser().isActive()) {
            throw new ForbiddenActionException(
                    "Your account is inactive"
            );
        }
    }

    public void validateDoctor(Doctor doctor) {

        if (!doctor.isActive()) {
            throw new BadRequestException(
                    "This doctor is not currently accepting appointments"
            );
        }
    }

    public void validateFutureDateTime(
            AppointmentBookingRequest request) {

        LocalDateTime requestedStart =
                LocalDateTime.of(
                        request.getAppointmentDate(),
                        request.getStartTime()
                );

        if (requestedStart.isBefore(LocalDateTime.now())) {
            throw new BadRequestException(
                    "Appointment date/time must be in the future"
            );
        }
    }

    public void validateSlot(
            LocalTime startTime,
            LocalTime endTime) {

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException(
                    "Appointment end time must be after start time"
            );
        }
    }
}