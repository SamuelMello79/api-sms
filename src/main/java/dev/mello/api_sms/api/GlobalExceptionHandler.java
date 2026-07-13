package dev.mello.api_sms.api;

import dev.mello.api_sms.api.dtos.ErrorResponseDTO;
import dev.mello.api_sms.infrastructure.exceptions.BadRequestException;
import dev.mello.api_sms.infrastructure.exceptions.NotFoundException;
import dev.mello.api_sms.infrastructure.exceptions.SmsGatewayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlerNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(NOT_FOUND)
                .body(build(NOT_FOUND, "Not Found", ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDTO> handlerBadRequestException(BadRequestException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(build(BAD_REQUEST, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(SmsGatewayException.class)
    public ResponseEntity<ErrorResponseDTO> handlerBadRequestException(SmsGatewayException ex) {
        return ResponseEntity.status(GATEWAY_TIMEOUT)
                .body(build(GATEWAY_TIMEOUT, "Gateway Exception", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handlerIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(build(BAD_REQUEST, "Bad Request", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handlerMethodArgumentValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        return ResponseEntity.status(BAD_REQUEST)
                .body(build(BAD_REQUEST, "Validation Failed", ex.getMessage(), errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handlerException() {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(build(INTERNAL_SERVER_ERROR, "Internal Error",
                        "An unexpected error occurred. Try again later"));
    }

    private static ErrorResponseDTO build(HttpStatus status, String error, String message) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .build();
    }

    private static ErrorResponseDTO build(HttpStatus status, String error, String message, List<String> errors) {
        return ErrorResponseDTO.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .errors(errors)
                .build();
    }
}
