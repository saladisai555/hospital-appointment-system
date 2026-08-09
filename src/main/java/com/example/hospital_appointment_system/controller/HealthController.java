package com.example.hospital_appointment_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        try (Connection connection = dataSource.getConnection()) {
            result.put("database", "CONNECTED");
            result.put("catalog", connection.getCatalog());
        } catch (Exception e) {
            result.put("database", "DISCONNECTED");
            result.put("error", e.getMessage());
        }
        return result;
    }
}