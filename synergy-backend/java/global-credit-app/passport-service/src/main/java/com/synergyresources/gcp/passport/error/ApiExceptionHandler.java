package com.synergyresources.gcp.passport.error;

import org.springframework.http.HttpStatus;
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

  @ExceptionHandler(PassportException.class)
  public ResponseEntity<Map<String, Object>> handle(PassportException e) {
    return ResponseEntity.status(e.getStatus())
        .body(Map.of("status", e.getStatus(), "error", e.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
    Map<String, String> fields = e.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(FieldError::getField, fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid",
            (a, b) -> a, LinkedHashMap::new));
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", 400);
    body.put("error", "Validation failed");
    body.put("fields", fields);
    return ResponseEntity.badRequest().body(body);
  }
}
