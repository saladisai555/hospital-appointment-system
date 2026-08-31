package com.example.hospital_appointment_system;

import com.example.hospital_appointment_system.dto.request.AppointmentBookingRequest;
import com.example.hospital_appointment_system.entity.*;
import com.example.hospital_appointment_system.exception.ConflictException;
import com.example.hospital_appointment_system.repository.*;
import com.example.hospital_appointment_system.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AppointmentConcurrencyTest {

    @Autowired private AppointmentService appointmentService;
    @Autowired private UserRepository userRepository;
    @Autowired private PatientRepository patientRepository;
    @Autowired private DoctorRepository doctorRepository;
    @Autowired private DepartmentRepository departmentRepository;
    @Autowired private DoctorAvailabilityRepository availabilityRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void twoSimultaneousBookingsForSameSlot_onlyOneSucceeds() throws Exception  {
        // ---- Arrange: one department, one doctor, availability covering the slot,
        // and TWO different patients trying to book the exact same slot at once ----
        Department department = new Department();
        department.setName("Concurrency-Test-Dept-" + System.nanoTime());
        department.setActive(true);
        departmentRepository.save(department);

        User doctorUser = newUser("doc-" + System.nanoTime() + "@test.com", Role.DOCTOR);
        Doctor doctor = new Doctor();
        doctor.setUser(doctorUser);
        doctor.setDepartment(department);
        doctor.setLicenseNumber("LIC-" + System.nanoTime());
        doctor.setConsultationFee(java.math.BigDecimal.valueOf(500));
        doctor.setActive(true);
        doctorRepository.save(doctor);

        // Availability rule covering a fixed future date's day-of-week, 09:00-10:00
        LocalDate targetDate = nextDateForDay(DayOfWeek.MONDAY);
        DoctorAvailability availability = new DoctorAvailability();
        availability.setDoctor(doctor);
        availability.setDayOfWeek(DayOfWeek.MONDAY);
        availability.setStartTime(LocalTime.of(9, 0));
        availability.setEndTime(LocalTime.of(10, 0));
        availability.setSlotDurationMinutes(30);
        availability.setActive(true);
        availabilityRepository.save(availability);

        Patient patientA = newPatient("patientA-" + System.nanoTime() + "@test.com");
        Patient patientB = newPatient("patientB-" + System.nanoTime() + "@test.com");

        AppointmentBookingRequest requestA = new AppointmentBookingRequest();
        requestA.setDoctorId(doctor.getId());
        requestA.setAppointmentDate(targetDate);
        requestA.setStartTime(LocalTime.of(9, 0));
        requestA.setReason("Concurrency test A");

        AppointmentBookingRequest requestB = new AppointmentBookingRequest();
        requestB.setDoctorId(doctor.getId());
        requestB.setAppointmentDate(targetDate);
        requestB.setStartTime(LocalTime.of(9, 0)); // SAME slot
        requestB.setReason("Concurrency test B");

        // ---- Act: fire both booking requests at the same time from separate threads ----
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        Callable<Void> taskA = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                appointmentService.book(patientA.getUser().getId(), requestA);
                successCount.incrementAndGet();
            } catch (ConflictException e) {
                conflictCount.incrementAndGet();
            }
            return null;
        };
        Callable<Void> taskB = () -> {
            readyLatch.countDown();
            startLatch.await();
            try {
                appointmentService.book(patientB.getUser().getId(), requestB);
                successCount.incrementAndGet();
            } catch (ConflictException e) {
                conflictCount.incrementAndGet();
            }
            return null;
        };

        Future<Void> futureA = executor.submit(taskA);
        Future<Void> futureB = executor.submit(taskB);

        readyLatch.await();       // wait until both threads are ready
        startLatch.countDown();   // release both at (almost) the same instant

        futureA.get(10, TimeUnit.SECONDS);
        futureB.get(10, TimeUnit.SECONDS);
        executor.shutdown();

        // ---- Assert: exactly one booking succeeded, the other got a conflict ----
        assertEquals(1, successCount.get(), "Exactly one booking should succeed");
        assertEquals(1, conflictCount.get(), "Exactly one booking should be rejected as a conflict");
    }

    private User newUser(String email, Role role) {
        User user = new User();
        user.setName("Test " + role);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode("Passw0rd!"));
        user.setRole(role);
        user.setActive(true);
        return userRepository.save(user);
    }

    private Patient newPatient(String email) {
        User user = newUser(email, Role.PATIENT);
        Patient patient = new Patient();
        patient.setUser(user);
        return patientRepository.save(patient);
    }

    private LocalDate nextDateForDay(DayOfWeek day) {
        LocalDate date = LocalDate.now().plusDays(1);
        while (date.getDayOfWeek() != day) {
            date = date.plusDays(1);
        }
        return date;
    }
}
