package com.boutique.user.controller;

import com.boutique.user.dto.UserResponse;
import com.boutique.user.entity.UserStatus;
import com.boutique.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private final UserService userService = mock(UserService.class);
    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new UserController(userService))
            .build();

    @Test
    void createUserReturns201AndLocation() throws Exception {
        UUID id = UUID.randomUUID();
        UserResponse response = new UserResponse(
                id,
                "khaja@example.com",
                "Khaja",
                "Mohinuddin",
                null,
                UserStatus.ACTIVE,
                Instant.parse("2026-08-04T00:00:00Z"),
                Instant.parse("2026-08-04T00:00:00Z"),
                0
        );

        when(userService.createUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "khaja@example.com",
                                  "firstName": "Khaja",
                                  "lastName": "Mohinuddin"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/users/" + id))
                .andExpect(jsonPath("$.email").value("khaja@example.com"));
    }
}
