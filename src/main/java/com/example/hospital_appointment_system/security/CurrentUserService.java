package com.example.hospital_appointment_system.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public Integer getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal()
                        instanceof CustomUserDetails userDetails)) {

            throw new IllegalStateException(
                    "No authenticated user found"
            );
        }

        return userDetails.getUserId();
    }
}