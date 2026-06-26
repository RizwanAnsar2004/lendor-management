package com.synergyresources.gcp.passport.error;

public class PassportException extends RuntimeException {
  private final int status;
  public PassportException(int status, String message) { super(message); this.status = status; }
  public int getStatus() { return status; }
}
