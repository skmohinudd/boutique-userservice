package com.boutique.user.service;

import com.boutique.user.dto.CreateUserRequest;
import com.boutique.user.dto.UpdateUserRequest;
import com.boutique.user.dto.UserResponse;
import com.boutique.user.entity.User;
import com.boutique.user.entity.UserStatus;
import com.boutique.user.exception.DuplicateUserException;
import com.boutique.user.exception.UserNotFoundException;
import com.boutique.user.exception.UserStateConflictException;
import com.boutique.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String email = normalizeEmail(request.email());
        String cognitoSub = normalizeNullable(request.cognitoSub());

        User user = new User(
                UUID.randomUUID(),
                cognitoSub,
                email,
                request.firstName().trim(),
                request.lastName().trim(),
                normalizeNullable(request.phoneNumber()),
                UserStatus.ACTIVE
        );

        try {
            // The database UNIQUE constraints are authoritative and race-safe.
            // saveAndFlush forces duplicate detection inside this request instead
            // of issuing two existence SELECTs before every INSERT.
            return toResponse(userRepository.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            throw new DuplicateUserException(
                    "A user already exists with the supplied email or Cognito identity."
            );
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(UUID userId) {
        return toResponse(findUser(userId));
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        User user = findUser(userId);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserStateConflictException("Only ACTIVE users can update their profile.");
        }

        user.updateProfile(
                request.firstName().trim(),
                request.lastName().trim(),
                normalizeNullable(request.phoneNumber())
        );

        return toResponse(user);
    }

    @Transactional
    public UserResponse deactivateUser(UUID userId) {
        User user = findUser(userId);

        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new UserStateConflictException("User is already INACTIVE.");
        }

        user.deactivate();
        return toResponse(user);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private String normalizeEmail(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getVersion()
        );
    }
}
