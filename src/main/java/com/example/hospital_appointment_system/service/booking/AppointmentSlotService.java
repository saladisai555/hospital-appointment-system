package com.example.hospital_appointment_system.service.booking;

import com.example.hospital_appointment_system.dto.request.AppointmentBookingRequest;
import com.example.hospital_appointment_system.entity.Doctor;
import com.example.hospital_appointment_system.entity.DoctorAvailability;
import com.example.hospital_appointment_system.exception.BadRequestException;
import com.example.hospital_appointment_system.repository.DoctorAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentSlotService {

    private final DoctorAvailabilityRepository availabilityRepository;

    public LocalTime calculateEndTime(
            Doctor doctor,
            AppointmentBookingRequest request) {

        List<DoctorAvailability> dayRules =
                availabilityRepository
                        .findByDoctorIdAndDayOfWeekAndActiveTrue(
                                doctor.getId(),
                                request.getAppointmentDate()
                                        .getDayOfWeek()
                        );

        DoctorAvailability matchingRule =
                dayRules.stream()
                        .filter(rule ->
                                !request.getStartTime()
                                        .isBefore(rule.getStartTime())
                                        &&
                                        request.getStartTime()
                                                .plusMinutes(
                                                        rule.getSlotDurationMinutes()
                                                )
                                                .compareTo(rule.getEndTime()) <= 0
                        )
                        .findFirst()
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "Doctor is not available at the requested time on this day"
                                )
                        );

        return request.getStartTime()
                .plusMinutes(
                        matchingRule.getSlotDurationMinutes()
                );
    }
}