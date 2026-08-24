package com.example.hospital_appointment_system.service.impl;

import com.example.hospital_appointment_system.dto.request.DoctorAvailabilityRequest;
import com.example.hospital_appointment_system.dto.response.DoctorAvailabilityResponse;
import com.example.hospital_appointment_system.entity.Doctor;
import com.example.hospital_appointment_system.entity.DoctorAvailability;
import com.example.hospital_appointment_system.exception.BadRequestException;
import com.example.hospital_appointment_system.exception.ConflictException;
import com.example.hospital_appointment_system.exception.ForbiddenActionException;
import com.example.hospital_appointment_system.exception.ResourceNotFoundException;
import com.example.hospital_appointment_system.mapper.AvailabilityMapper;
import com.example.hospital_appointment_system.repository.DoctorAvailabilityRepository;
import com.example.hospital_appointment_system.repository.DoctorRepository;
import com.example.hospital_appointment_system.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {

    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorRepository doctorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DoctorAvailabilityResponse> getByDoctor(Integer doctorId) {
        return availabilityRepository.findByDoctorIdAndActiveTrue(doctorId).stream()
                .map(AvailabilityMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorAvailabilityResponse> getByDoctorAndDate(Integer doctorId, LocalDate date) {
        return availabilityRepository.findByDoctorIdAndDayOfWeekAndActiveTrue(doctorId, date.getDayOfWeek()).stream()
                .map(AvailabilityMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DoctorAvailabilityResponse create(Integer doctorId, DoctorAvailabilityRequest request) {
        Doctor doctor = findDoctor(doctorId);
        validateTimeRange(request);

        if (availabilityRepository.existsOverlapping(doctorId, request.getDayOfWeek(),
                request.getStartTime(), request.getEndTime(), null)) {
            throw new ConflictException("This overlaps an existing availability rule for that day");
        }

        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setSlotDurationMinutes(request.getSlotDurationMinutes());
        availability.setActive(true);

        return AvailabilityMapper.toResponse(availabilityRepository.save(availability));
    }

    @Override
    @Transactional
    public DoctorAvailabilityResponse update(Integer doctorId, Integer availabilityId, DoctorAvailabilityRequest request) {
        DoctorAvailability availability = findOwnedAvailability(doctorId, availabilityId);
        validateTimeRange(request);

        if (availabilityRepository.existsOverlapping(doctorId, request.getDayOfWeek(),
                request.getStartTime(), request.getEndTime(), availabilityId)) {
            throw new ConflictException("This overlaps an existing availability rule for that day");
        }

        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setSlotDurationMinutes(request.getSlotDurationMinutes());

        return AvailabilityMapper.toResponse(availability);
    }

    @Override
    @Transactional
    public void delete(Integer doctorId, Integer availabilityId) {
        findOwnedAvailability(doctorId, availabilityId).setActive(false);
    }

    private void validateTimeRange(DoctorAvailabilityRequest request) {
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BadRequestException("startTime must be before endTime");
        }
        if (request.getSlotDurationMinutes() <= 0) {
            throw new BadRequestException("slotDurationMinutes must be positive");
        }
    }

    private Doctor findDoctor(Integer doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + doctorId));
    }

private DoctorAvailability findOwnedAvailability(Integer doctorId, Integer availabilityId) {
    DoctorAvailability availability = availabilityRepository.findById(availabilityId)
            .orElseThrow(() -> new ResourceNotFoundException("Availability rule not found: " + availabilityId));
    // Spec rule 9: "Doctor can modify only their own availability."
    if (!availability.getDoctor().getId().equals(doctorId)) {
        throw new ForbiddenActionException("You can only modify your own availability");
    }
    return availability;
}
}