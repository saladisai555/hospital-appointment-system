package com.example.hospital_appointment_system.service.booking;

import com.example.hospital_appointment_system.entity.AppointmentStatus;
import com.example.hospital_appointment_system.exception.ConflictException;
import com.example.hospital_appointment_system.repository.AppointmentRepository;
import com.example.hospital_appointment_system.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentConflictService {

    private static final List<AppointmentStatus> INACTIVE_STATUSES =
            List.of(
                    AppointmentStatus.CANCELLED,
                    AppointmentStatus.REJECTED
            );

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public void validateNoConflict(
            Integer patientId,
            Integer doctorId,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime) {

        // Keep pessimistic locking for concurrent bookings
        lockDoctor(doctorId);

        // Check overlapping appointments directly in the database
        boolean overlapExists =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndStatusNotInAndStartTimeLessThanAndEndTimeGreaterThan(
                                doctorId,
                                appointmentDate,
                                INACTIVE_STATUSES,
                                endTime,
                                startTime
                        );

        if (overlapExists) {
            throw new ConflictException(
                    "This slot is already booked. Please choose another time."
            );
        }

        // Check duplicate booking by the same patient
        boolean duplicateBooking =
                appointmentRepository
                        .existsByPatientIdAndDoctorIdAndAppointmentDateAndStartTimeAndStatusNotIn(
                                patientId,
                                doctorId,
                                appointmentDate,
                                startTime,
                                INACTIVE_STATUSES
                        );

        if (duplicateBooking) {
            throw new ConflictException(
                    "You already have a booking with this doctor at this time"
            );
        }
    }

    private void lockDoctor(Integer doctorId) {

        try {
            doctorRepository.findByIdForUpdate(doctorId);
        } catch (jakarta.persistence.PessimisticLockException |
                 jakarta.persistence.LockTimeoutException e) {

            throw new ConflictException(
                    "This doctor is currently handling another booking. Please try again."
            );
        }
    }
}