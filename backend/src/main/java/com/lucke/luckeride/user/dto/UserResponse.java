package com.lucke.luckeride.user.dto;

import com.lucke.luckeride.user.entity.UserRole;
import com.lucke.luckeride.user.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        UserRole role,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}