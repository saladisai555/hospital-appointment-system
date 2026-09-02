package com.example.hospital_appointment_system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Hospital / Clinic Appointment Booking System API",
                version = "1.0",
                description = "REST API for patient/doctor/admin appointment booking, "
                        + "with JWT authentication and role-based access control.",
                contact = @Contact(name = "Project Owner")
        ),
        servers = { @Server(url = "http://localhost:8080", description = "Local dev server") }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Paste the JWT returned from /api/auth/login (without the word 'Bearer')"
)
@Configuration
public class OpenApiConfig {
}