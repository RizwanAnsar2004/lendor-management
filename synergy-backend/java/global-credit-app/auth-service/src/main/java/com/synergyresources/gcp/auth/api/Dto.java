package com.synergyresources.gcp.auth.api;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public class Dto {

  public static class OtpRequestBody {
    @NotBlank @Email public String email;
  }

  public static class OtpVerifyBody {
    @NotBlank @Email public String email;
    @NotBlank @Size(min = 6, max = 6) public String code;
  }

  public static class RegisterBody {
    @NotBlank @Email public String email;
    @NotBlank public String firstName;
    @NotBlank public String lastName;
    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") public String password;
    @Past public LocalDate dob;
  }

  public static class LoginBody {
    @NotBlank @Email public String email;
    @NotBlank public String password;
  }

  public static class MessageResponse {
    public String message;
    public MessageResponse(String message) { this.message = message; }
  }

  public static class OtpVerifyResponse {
    public boolean verified;
    public OtpVerifyResponse(boolean verified) { this.verified = verified; }
  }

  public static class RegisterResponse {
    public UUID userId;
    public String status;
    public RegisterResponse(UUID userId, String status) { this.userId = userId; this.status = status; }
  }

  public static class LoginResponse {
    public String accessToken;
    public String tokenType = "Bearer";
    public long expiresInSeconds;
    public LoginResponse(String accessToken, long expiresInSeconds) {
      this.accessToken = accessToken;
      this.expiresInSeconds = expiresInSeconds;
    }
  }

  public static class MeResponse {
    public UUID userId;
    public String email;
    public String firstName;
    public String lastName;
    public MeResponse(UUID userId, String email, String firstName, String lastName) {
      this.userId = userId;
      this.email = email;
      this.firstName = firstName;
      this.lastName = lastName;
    }
  }
}
