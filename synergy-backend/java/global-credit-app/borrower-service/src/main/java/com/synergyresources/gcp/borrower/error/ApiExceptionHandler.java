package com.synergyresources.gcp.borrower.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler(BorrowerException.class)
  public ResponseEntity<Map<String, Object>> handleBorrower(BorrowerException ex) {
    return ResponseEntity.status(ex.getStatusCode())
        .body(error(ex.getStatusCode(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, String> fields = new HashMap<>();
    for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
      fields.put(fe.getField(), fe.getDefaultMessage());
    }
    Map<String, Object> body = error(400, "Validation failed");
    body.put("fields", fields);
    return ResponseEntity.badRequest().body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
    return ResponseEntity.status(500).body(error(500, "Internal server error"));
  }

  private Map<String, Object> error(int status, String message) {
    Map<String, Object> m = new HashMap<>();
    m.put("status", status);
    m.put("error", message);
    return m;
  }
}
