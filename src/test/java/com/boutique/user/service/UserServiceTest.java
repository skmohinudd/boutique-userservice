package com.boutique.user.service;

import com.boutique.user.dto.CreateUserRequest;
import com.boutique.user.dto.UserResponse;
import com.boutique.user.entity.User;
import com.boutique.user.exception.DuplicateUserException;
import com.boutique.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private final UserRepository repository = mock(UserRepository.class);
    private final UserService service = new UserService(repository);

    @Test
    void createsNormalizedActiveUser() {

        CreateUserRequest request = new CreateUserRequest(
                "  KHAJA@example.com ",
                " Khaja ",
                " Mohinuddin ",
                " +916300646255 ",
                null
        );

        when(repository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = service.createUser(request);

        assertEquals("khaja@example.com", response.email());
        assertEquals("Khaja", response.firstName());
        assertEquals("Mohinuddin", response.lastName());
        assertNotNull(response.id());

        verify(repository, times(1))
                .saveAndFlush(any(User.class));

        verifyNoMoreInteractions(repository);
    }

    @Test
    void rejectsDuplicateUserWhenDatabaseUniqueConstraintFails() {

        CreateUserRequest request = new CreateUserRequest(
                "khaja@example.com",
                "Khaja",
                "Mohinuddin",
                null,
                null
        );

        when(repository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "duplicate key violates unique constraint"
                ));

        assertThrows(
                DuplicateUserException.class,
                () -> service.createUser(request)
        );

        verify(repository, times(1))
                .saveAndFlush(any(User.class));

        verifyNoMoreInteractions(repository);
    }
}
