package com.boutique.user.dto;

import com.boutique.user.entity.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phoneNumber,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt,
        long version
) {
}
