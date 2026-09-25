package com.example.hospital_appointment_system.service.impl;

import com.example.hospital_appointment_system.entity.Role;
import com.example.hospital_appointment_system.entity.User;
import com.example.hospital_appointment_system.exception.ConflictException;
import com.example.hospital_appointment_system.repository.UserRepository;
import com.example.hospital_appointment_system.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createAccount(
            String name,
            String email,
            String password,
            Role role,
            String phone) {

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(
                    "An account with this email already exists"
            );
        }

        User user = new User();

        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(
                passwordEncoder.encode(password)
        );
        user.setRole(role);
        user.setPhone(phone);
        user.setActive(true);

        return userRepository.save(user);
    }
}