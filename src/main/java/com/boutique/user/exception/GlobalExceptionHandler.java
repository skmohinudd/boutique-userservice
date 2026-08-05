package com.boutique.user.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    ProblemDetail handleNotFound(UserNotFoundException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.NOT_FOUND,
                "User not found",
                exception.getMessage(),
                "/problems/user-not-found",
                request
        );
    }

    @ExceptionHandler(DuplicateUserException.class)
    ProblemDetail handleDuplicate(DuplicateUserException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                "Duplicate user",
                exception.getMessage(),
                "/problems/duplicate-user",
                request
        );
    }

    @ExceptionHandler(UserStateConflictException.class)
    ProblemDetail handleStateConflict(UserStateConflictException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                "User state conflict",
                exception.getMessage(),
                "/problems/user-state-conflict",
                request
        );
    }

    @ExceptionHandler({OptimisticLockException.class})
    ProblemDetail handleOptimisticLock(Exception exception, HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                "Concurrent update conflict",
                "The user was modified by another request. Reload and retry.",
                "/problems/concurrent-update",
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleIntegrity(DataIntegrityViolationException exception, HttpServletRequest request) {
        return problem(
                HttpStatus.CONFLICT,
                "Data conflict",
                "The request conflicts with an existing user.",
                "/problems/data-conflict",
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        ProblemDetail detail = problem(
                HttpStatus.BAD_REQUEST,
                "Invalid request",
                "Request validation failed.",
                "/problems/validation-error",
                request
        );

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        detail.setProperty("errors", errors);
        return detail;
    }

    private ProblemDetail problem(
            HttpStatus status,
            String title,
            String detail,
            String type,
            HttpServletRequest request
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create(type));
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }
}
