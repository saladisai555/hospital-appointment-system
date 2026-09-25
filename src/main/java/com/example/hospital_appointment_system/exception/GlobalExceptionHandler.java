package com.example.hospital_appointment_system.exception;

import com.example.hospital_appointment_system.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {

        log.warn(
                "Resource not found: {} | Path: {}",
                ex.getMessage(),
                getPath(request)
        );

        return build(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex,
            WebRequest request) {

        log.warn(
                "Conflict: {} | Path: {}",
                ex.getMessage(),
                getPath(request)
        );

        return build(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            WebRequest request) {

        log.warn(
                "Bad request: {} | Path: {}",
                ex.getMessage(),
                getPath(request)
        );

        return build(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler({
            ForbiddenActionException.class,
            AccessDeniedException.class
    })
    public ResponseEntity<ErrorResponse> handleForbidden(
            RuntimeException ex,
            WebRequest request) {

        log.warn(
                "Forbidden request: {} | Path: {}",
                ex.getMessage(),
                getPath(request)
        );

        return build(
                HttpStatus.FORBIDDEN,
                ex.getMessage(),
                request,
                null
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            BadCredentialsException ex,
            WebRequest request) {

        log.warn(
                "Authentication failed | Path: {}",
                getPath(request)
        );

        return build(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password",
                request,
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        List<String> details =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError ->
                                fieldError.getField()
                                        + ": "
                                        + fieldError.getDefaultMessage()
                        )
                        .toList();

        log.warn(
                "Validation failed | Path: {} | Details: {}",
                getPath(request),
                details
        );

        return build(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                request,
                details
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            WebRequest request) {

        /*
         * Log the complete exception on the server.
         * Do not expose internal exception details to the client.
         */
        log.error(
                "Unexpected error | Path: {}",
                getPath(request),
                ex
        );

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request,
                null
        );
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String message,
            WebRequest request,
            List<String> details) {

        ErrorResponse error =
                ErrorResponse.builder()
                        .timestamp(LocalDateTime.now())
                        .status(status.value())
                        .error(status.getReasonPhrase())
                        .message(message)
                        .path(getPath(request))
                        .details(details)
                        .build();

        return new ResponseEntity<>(error, status);
    }

    private String getPath(WebRequest request) {
        return request
                .getDescription(false)
                .replace("uri=", "");
    }
}