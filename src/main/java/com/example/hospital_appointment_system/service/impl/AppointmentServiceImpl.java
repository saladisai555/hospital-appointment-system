package com.example.hospital_appointment_system.service.impl;

import com.example.hospital_appointment_system.dto.request.AppointmentBookingRequest;
import com.example.hospital_appointment_system.dto.request.AppointmentStatusUpdateRequest;
import com.example.hospital_appointment_system.dto.response.AppointmentResponse;
import com.example.hospital_appointment_system.entity.*;
import com.example.hospital_appointment_system.exception.*;
import com.example.hospital_appointment_system.mapper.AppointmentMapper;
import com.example.hospital_appointment_system.repository.*;
import com.example.hospital_appointment_system.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorAvailabilityRepository availabilityRepository;

    private static final List<AppointmentStatus> INACTIVE_STATUSES =
            List.of(AppointmentStatus.CANCELLED, AppointmentStatus.REJECTED);


    @Override
    @Transactional
    public AppointmentResponse book(Integer patientUserId, AppointmentBookingRequest request) {
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        if (!patient.getUser().isActive()) {
            throw new ForbiddenActionException("Your account is inactive");
        }

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + request.getDoctorId()));
        if (!doctor.isActive()) {
            throw new BadRequestException("This doctor is not currently accepting appointments");
        }

        // Rule 3: valid future date/time
        LocalDateTime requestedStart = LocalDateTime.of(request.getAppointmentDate(), request.getStartTime());
        if (requestedStart.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Appointment date/time must be in the future");
        }

        // Rule 4: requested time must fall inside doctor availability
        List<DoctorAvailability> dayRules = availabilityRepository
                .findByDoctorIdAndDayOfWeekAndActiveTrue(doctor.getId(), request.getAppointmentDate().getDayOfWeek());

        DoctorAvailability matchingRule = dayRules.stream()
                .filter(rule -> !request.getStartTime().isBefore(rule.getStartTime())
                        && request.getStartTime().plusMinutes(rule.getSlotDurationMinutes()).compareTo(rule.getEndTime()) <= 0)
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        "Doctor is not available at the requested time on this day"));

        LocalTime endTime = request.getStartTime().plusMinutes(matchingRule.getSlotDurationMinutes());

        // ==== CONCURRENCY-SAFE SECTION ====
        // Acquire a pessimistic write lock on ALL of this doctor's appointments for this
        // date BEFORE checking for overlap. Any other transaction trying to book the same
        // doctor/date will block here until this transaction commits or rolls back - so
        // the "check overlap, then insert" sequence below becomes effectively atomic.
        List<Appointment> lockedExistingAppointments;
        try {
            lockedExistingAppointments = appointmentRepository
                    .lockAppointmentsForDoctorAndDate(doctor.getId(), request.getAppointmentDate());
        } catch (jakarta.persistence.PessimisticLockException | jakarta.persistence.LockTimeoutException e) {
            // Another transaction is holding the lock and we timed out waiting for it.
            throw new ConflictException("This slot is currently being booked by someone else. Please try again.");
        }

        boolean overlapExists = lockedExistingAppointments.stream()
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED && a.getStatus() != AppointmentStatus.REJECTED)
                .anyMatch(a -> a.getStartTime().isBefore(endTime) && a.getEndTime().isAfter(request.getStartTime()));

        // Rule 5: same doctor cannot have two active appointments in the same slot
        if (overlapExists) {
            throw new ConflictException("This slot is already booked. Please choose another time.");
        }

        // Rule 6: patient should not duplicate-book the same doctor/date/time
        if (appointmentRepository.existsByPatientIdAndDoctorIdAndAppointmentDateAndStartTimeAndStatusNotIn(
                patient.getId(), doctor.getId(), request.getAppointmentDate(), request.getStartTime(), INACTIVE_STATUSES)) {
            throw new ConflictException("You already have a booking with this doctor at this time");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setReason(request.getReason());

        Appointment saved = appointmentRepository.save(appointment);
        // Email confirmation wired up in Phase 15 - not part of this transaction.
        return AppointmentMapper.toResponse(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getMyAppointments(Integer patientUserId) {
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDescStartTimeDesc(patient.getId()).stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getDoctorAppointments(Integer doctorUserId) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDescStartTimeDesc(doctor.getId()).stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppointmentResponse cancelByPatient(Integer patientUserId, Integer appointmentId) {
        Patient patient = patientRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient profile not found"));
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + appointmentId));

        // Rule 10: patient can cancel only their own appointment
        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new ForbiddenActionException("You can only cancel your own appointments");
        }
        // Rule 8: completed/cancelled cannot be modified as active bookings
        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("This appointment can no longer be cancelled");
        }

        // Rule 7: cancellation releases the slot - existsOverlappingAppointment already
        // excludes CANCELLED/REJECTED, so no extra cleanup step is needed.
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return AppointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional
    public AppointmentResponse updateStatusByDoctor(Integer doctorUserId, Integer appointmentId,
                                                    AppointmentStatusUpdateRequest request) {
        Doctor doctor = doctorRepository.findByUserId(doctorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + appointmentId));

        if (!appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new ForbiddenActionException("You can only update your own appointments");
        }
        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new BadRequestException("This appointment can no longer be modified");
        }

        appointment.setStatus(request.getStatus());
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }
        return AppointmentMapper.toResponse(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAll() {
        return appointmentRepository.findAll().stream()
                .map(AppointmentMapper::toResponse)
                .toList();
    }
}