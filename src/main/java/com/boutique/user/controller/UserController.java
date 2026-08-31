package com.boutique.user.controller;

import com.boutique.user.auth.CognitoUserInfoClient;
import com.boutique.user.dto.CreateUserRequest;
import com.boutique.user.dto.UpdateUserRequest;
import com.boutique.user.dto.UserResponse;
import com.boutique.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final CognitoUserInfoClient cognitoUserInfoClient;

    public UserController(
            UserService userService,
            CognitoUserInfoClient cognitoUserInfoClient
    ) {
        this.userService = userService;
        this.cognitoUserInfoClient = cognitoUserInfoClient;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        UserResponse response = userService.createUser(request);

        URI location = URI.create(
                "/api/v1/users/" + response.id()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PostMapping("/me")
    public UserResponse syncCurrentUser(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateUserRequest request
    ) {
        String subject = jwt.getSubject();

        String email = jwt.getClaimAsString("email");

        if (email == null || email.isBlank()) {
            email = cognitoUserInfoClient
                    .verifiedEmail(jwt.getTokenValue());
        }

        return userService.syncCognitoUser(
                subject,
                email,
                request
        );
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(
            @PathVariable UUID userId
    ) {
        return userService.getUser(userId);
    }

    @PutMapping("/{userId}")
    public UserResponse updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        return userService.updateUser(
                userId,
                request
        );
    }

    @PostMapping("/{userId}/deactivate")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse deactivateUser(
            @PathVariable UUID userId
    ) {
        return userService.deactivateUser(userId);
    }
}