package com.synergyresources.gcp.lender.error;

public class LenderException extends RuntimeException {
  private final int statusCode;

  public LenderException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public int getStatusCode() { return statusCode; }
}
