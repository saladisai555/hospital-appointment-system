package com.example.hospital_appointment_system.config;

import com.example.hospital_appointment_system.security.JwtAuthenticationFilter;
import com.example.hospital_appointment_system.security.handler.RestAccessDeniedHandler;
import com.example.hospital_appointment_system.security.handler.RestAuthenticationEntryPoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.example.hospital_appointment_system.dto.response.ErrorResponse;

import java.time.LocalDateTime;
import java.util.List;
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;

    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                authenticationEntryPoint
                        )

                        .accessDeniedHandler(
                                accessDeniedHandler
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        // Public
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/health").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Doctor-only
                        .requestMatchers("/api/doctor/availability/**").hasRole("DOCTOR")
                        .requestMatchers("/api/doctor/appointments/**").hasRole("DOCTOR")

                        // Patient-only
                        .requestMatchers("/api/appointments/my").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.PATCH, "/api/appointments/*/cancel").hasRole("PATIENT")

                        // Admin-only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Everything else under /api just needs to be logged in as any role
                        .requestMatchers("/api/**").authenticated()

                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    private void writeError(HttpServletResponse response, HttpStatus status, String message, String path) throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
                .writeValue(response.getWriter(), error);
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}