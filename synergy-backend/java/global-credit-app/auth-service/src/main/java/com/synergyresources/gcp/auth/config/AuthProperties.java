package com.synergyresources.gcp.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gcp")
public class AuthProperties {

  private final Jwt jwt = new Jwt();
  private final Otp otp = new Otp();
  private final Mail mail = new Mail();

  public Jwt getJwt() { return jwt; }
  public Otp getOtp() { return otp; }
  public Mail getMail() { return mail; }

  public static class Jwt {
    private String secret = "change-me-dev-secret-at-least-32-bytes-long!";
    private long expirySeconds = 3600;
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }
    public long getExpirySeconds() { return expirySeconds; }
    public void setExpirySeconds(long expirySeconds) { this.expirySeconds = expirySeconds; }
  }

  public static class Otp {
    private long expirySeconds = 600;
    private int maxAttempts = 5;
    public long getExpirySeconds() { return expirySeconds; }
    public void setExpirySeconds(long expirySeconds) { this.expirySeconds = expirySeconds; }
    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
  }

  public static class Mail {
    private String from = "no-reply@gcp.local";
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
  }
}
