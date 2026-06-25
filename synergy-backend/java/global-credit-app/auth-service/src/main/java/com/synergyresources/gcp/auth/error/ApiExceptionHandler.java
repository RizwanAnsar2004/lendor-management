package com.synergyresources.gcp.auth.error;

import com.synergyresources.gcp.auth.service.AuthException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(AuthException.class)
  public ResponseEntity<Map<String, Object>> handleAuth(AuthException ex) {
    return ResponseEntity.status(ex.getStatusCode())
        .body(Map.of("error", ex.getMessage(), "status", ex.getStatusCode()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
            (a, b) -> a, LinkedHashMap::new));
    return ResponseEntity.badRequest()
        .body(Map.of("error", "Validation failed", "fields", fieldErrors, "status", 400));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    return ResponseEntity.internalServerError()
        .body(Map.of("error", "Internal server error", "status", 500));
  }
}
