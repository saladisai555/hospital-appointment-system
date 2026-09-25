package com.example.hospital_appointment_system.service;

import com.example.hospital_appointment_system.entity.User;
import com.example.hospital_appointment_system.entity.Role;

public interface UserAccountService {

    User createAccount(
            String name,
            String email,
            String password,
            Role role,
            String phone
    );
}