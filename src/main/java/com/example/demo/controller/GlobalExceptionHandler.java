package com.example.demo.controller;

import com.example.demo.exception.IngredientNotInRecipeException;
import com.example.demo.exception.RecipeNotFoundException;
import com.example.demo.models.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest httpServletRequest) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        Map<String, String> errorDetails = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Optional.ofNullable(error.getDefaultMessage()).orElse("Invalid value"),
                        (existing, replacement) -> existing
                ));

        return ResponseEntity
                .status(httpStatus)
                .body(errorResponse(httpStatus, "Validation failed", errorDetails, httpServletRequest.getRequestURI()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(httpStatus)
                .body(errorResponse(httpStatus, "Malformed JSON request", ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        Map<String, String> details = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));
        return ResponseEntity.status(httpStatus)
                .body(errorResponse(httpStatus, "Validation failed", details, request.getRequestURI()));
    }

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ApiError> handleRecipeNotFoundException(RecipeNotFoundException ex, HttpServletRequest httpServletRequest) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(httpStatus)
                .body(errorResponse(httpStatus, ex.getMessage(), null, httpServletRequest.getRequestURI()));
    }

    @ExceptionHandler(IngredientNotInRecipeException.class)
    public ResponseEntity<ApiError> handleIngredientNotInRecipeException(IngredientNotInRecipeException ex, HttpServletRequest httpServletRequest) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(httpStatus)
                .body(errorResponse(httpStatus, ex.getMessage(), null, httpServletRequest.getRequestURI()));
    }

    private ApiError errorResponse(HttpStatus status, String message, Object details, String path) {
        return new ApiError()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .details(details)
                .path(path);
    }
}
