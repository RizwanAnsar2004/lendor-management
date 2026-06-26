package com.synergyresources.gcp.auth.api;

import com.synergyresources.gcp.auth.service.AuthService;
import com.synergyresources.gcp.auth.service.OtpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final OtpService otpService;
  private final AuthService authService;

  public AuthController(OtpService otpService, AuthService authService) {
    this.otpService = otpService;
    this.authService = authService;
  }

  @PostMapping("/otp/request")
  public ResponseEntity<Dto.MessageResponse> requestOtp(@Valid @RequestBody Dto.OtpRequestBody body) {
    otpService.requestOtp(body.email);
    return ResponseEntity.ok(new Dto.MessageResponse("OTP sent to " + body.email));
  }

  @PostMapping("/otp/verify")
  public ResponseEntity<Dto.OtpVerifyResponse> verifyOtp(@Valid @RequestBody Dto.OtpVerifyBody body) {
    otpService.verifyOtp(body.email, body.code);
    return ResponseEntity.ok(new Dto.OtpVerifyResponse(true));
  }

  @PostMapping("/register")
  public ResponseEntity<Dto.RegisterResponse> register(@Valid @RequestBody Dto.RegisterBody body) {
    return ResponseEntity.ok(authService.register(body));
  }

  @PostMapping("/login")
  public ResponseEntity<Dto.LoginResponse> login(@Valid @RequestBody Dto.LoginBody body) {
    return ResponseEntity.ok(authService.login(body));
  }

  @GetMapping("/me")
  public ResponseEntity<Dto.MeResponse> me(
      @RequestHeader(value = "Authorization", required = false) String authorization) {
    return ResponseEntity.ok(authService.me(authorization));
  }
}
