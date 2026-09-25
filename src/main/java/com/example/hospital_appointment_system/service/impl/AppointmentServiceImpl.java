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
import com.example.hospital_appointment_system.service.booking.AppointmentBookingValidator;
import com.example.hospital_appointment_system.service.booking.AppointmentConflictService;
import com.example.hospital_appointment_system.service.booking.AppointmentSlotService;
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentBookingValidator bookingValidator;
    private final AppointmentSlotService slotService;
    private final AppointmentConflictService conflictService;


    @Override
    @Transactional
    public AppointmentResponse book(
            Integer patientUserId,
            AppointmentBookingRequest request) {

        Patient patient = patientRepository
                .findByUserId(patientUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient profile not found"
                        )
                );

        bookingValidator.validatePatient(patient);

        Doctor doctor = doctorRepository
                .findById(request.getDoctorId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found: "
                                        + request.getDoctorId()
                        )
                );

        bookingValidator.validateDoctor(doctor);

        bookingValidator.validateFutureDateTime(request);

        LocalTime endTime =
                slotService.calculateEndTime(
                        doctor,
                        request
                );

        bookingValidator.validateSlot(
                request.getStartTime(),
                endTime
        );

        conflictService.validateNoConflict(
                patient.getId(),
                doctor.getId(),
                request.getAppointmentDate(),
                request.getStartTime(),
                endTime
        );

        Appointment appointment = new Appointment();

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(
                request.getAppointmentDate()
        );
        appointment.setStartTime(
                request.getStartTime()
        );
        appointment.setEndTime(endTime);
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setReason(request.getReason());

        Appointment saved =
                appointmentRepository.save(appointment);

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