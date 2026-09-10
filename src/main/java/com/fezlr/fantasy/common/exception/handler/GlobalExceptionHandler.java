package com.fezlr.fantasy.common.exception.handler;

import com.fezlr.fantasy.common.exception.AccessDeniedException;
import com.fezlr.fantasy.common.exception.NotEnoughFantasyPointsException;
import com.fezlr.fantasy.common.exception.PackShopIsNotAvailableException;
import com.fezlr.fantasy.common.exception.SourceIdNotFoundException;
import com.fezlr.fantasy.common.exception.dto.ErrorResponseDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception e) {
        var message = ErrorResponseDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Unexpected error occurred")
                .timestamp(Instant.now().toString())
                .build();

        log.warn("Validation failed: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(ConstraintViolationException e) {
        var message = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad request")
                .message("Validation failed for one or more fields")
                .timestamp(Instant.now().toString())
                .build();

        log.warn("Validation failed: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingHeader(MissingRequestHeaderException e) {
        var message = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad request")
                .message("Required request header is missing")
                .timestamp(Instant.now().toString())
                .build();

        log.warn("Validation failed: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponseDto> handleSecurityException(SecurityException e) {
        var message = ErrorResponseDto.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Invalid authentication data")
                .timestamp(Instant.now().toString())
                .build();

        log.warn("Validation failed: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(message);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException e) {
        var message = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not found")
                .message("Entity is not found")
                .timestamp(Instant.now().toString())
                .build();

        log.warn("Validation failed: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(message);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(NoResourceFoundException e) {
        var message = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not found")
                .message("Resource is not found")
                .timestamp(Instant.now().toString())
                .build();

        log.warn("Validation failed: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(message);
    }

    @ExceptionHandler(SourceIdNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleSourceIdNotFoundException(SourceIdNotFoundException e) {
        var message = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not found")
                .message("Value is not found")
                .timestamp(Instant.now().toString())
                .build();

        log.warn("Validation failed: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(message);
    }

    @ExceptionHandler(NotEnoughFantasyPointsException.class)
    public ResponseEntity<ErrorResponseDto> handleNotEnoughFantasyPointsException(NotEnoughFantasyPointsException e) {
        var message = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Insufficient funds")
                .message("Not enough currency")
                .timestamp(Instant.now().toString())
                .build();

        log.warn("Validation failed: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(message);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
        var message = ErrorResponseDto.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("Forbidden")
                .message("Insufficient permissions")
                .timestamp(Instant.now().toString())
                .build();

        log.warn("Validation failed: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(message);
    }

    @ExceptionHandler(PackShopIsNotAvailableException.class)
    public ResponseEntity<ErrorResponseDto> handlePackShopIsNotAvailableException(PackShopIsNotAvailableException e) {
        var message = ErrorResponseDto.builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("Unavailable")
                .message("Service unavailable")
                .timestamp(Instant.now().toString())
                .build();

        log.warn("Validation failed: {}", message, e);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(message);
    }
}
