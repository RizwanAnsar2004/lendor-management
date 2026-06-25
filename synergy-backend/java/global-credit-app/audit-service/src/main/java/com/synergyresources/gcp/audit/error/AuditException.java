package com.synergyresources.gcp.audit.error;

public class AuditException extends RuntimeException {
  private final int statusCode;

  public AuditException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() { return statusCode; }
}
