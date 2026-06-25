package com.synergyresources.gcp.audit.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(AuditException.class)
  public ResponseEntity<Map<String, Object>> handle(AuditException ex) {
    return ResponseEntity.status(ex.getStatusCode())
        .body(Map.of("error", ex.getMessage(), "status", ex.getStatusCode()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .findFirst()
        .orElse("Validation failed");
    return ResponseEntity.badRequest().body(Map.of("error", msg, "status", 400));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    return ResponseEntity.internalServerError()
        .body(Map.of("error", ex.getMessage(), "status", 500));
  }
}
