package com.synergyresources.gcp.borrower.error;

public class BorrowerException extends RuntimeException {
  private final int statusCode;

  public BorrowerException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() { return statusCode; }
}
