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

    private static final List<String> INACTIVE_STATUSES =
            List.of(
                    AppointmentStatus.CANCELLED.name(),
                    AppointmentStatus.REJECTED.name()
            );

    private final AppointmentRepository appointmentRepository;

    private final DoctorRepository doctorRepository;

    public void validateNoConflict(
            Integer patientId,
            Integer doctorId,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime) {

        // Lock doctor to protect concurrent bookings
        lockDoctor(doctorId);

        // Check whether another active appointment overlaps this slot
        long overlapCount =
                appointmentRepository
                        .countOverlappingAppointments(
                                doctorId,
                                appointmentDate,
                                INACTIVE_STATUSES,
                                endTime,
                                startTime
                        );

        if (overlapCount > 0) {
            throw new ConflictException(
                    "This slot is already booked. Please choose another time."
            );
        }

        // Check whether the same patient already booked
        // the same doctor at the same time
        long duplicateBookingCount =
                appointmentRepository
                        .countDuplicateBookings(
                                patientId,
                                doctorId,
                                appointmentDate,
                                startTime,
                                INACTIVE_STATUSES
                        );

        if (duplicateBookingCount > 0) {
            throw new ConflictException(
                    "You already have a booking with this doctor at this time."
            );
        }
    }

    private void lockDoctor(Integer doctorId) {

        try {

            doctorRepository.findByIdForUpdate(doctorId);

        } catch (
                jakarta.persistence.PessimisticLockException |
                jakarta.persistence.LockTimeoutException e
        ) {

            throw new ConflictException(
                    "This doctor is currently handling another booking. Please try again."
            );
        }
    }
}