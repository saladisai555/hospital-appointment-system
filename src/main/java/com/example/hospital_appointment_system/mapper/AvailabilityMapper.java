package com.example.hospital_appointment_system.mapper;
import com.example.hospital_appointment_system.dto.response.DoctorAvailabilityResponse;
import com.example.hospital_appointment_system.entity.DoctorAvailability;

public class AvailabilityMapper {

    private AvailabilityMapper() {}

    public static DoctorAvailabilityResponse toResponse(DoctorAvailability availability) {
        if (availability == null) return null;
        return DoctorAvailabilityResponse.builder()
                .id(availability.getId())
                .doctorId(availability.getDoctor().getId())
                .dayOfWeek(availability.getDayOfWeek())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .slotDurationMinutes(availability.getSlotDurationMinutes())
                .active(availability.isActive())
                .build();
    }
}