package com.synergyresources.gcp.auth.service;

import com.synergyresources.gcp.auth.api.Dto;
import com.synergyresources.gcp.auth.model.AppUser;
import com.synergyresources.gcp.auth.repo.AppUserRepo;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

  private final AppUserRepo userRepo;
  private final OtpService otpService;
  private final PasswordEncoder encoder;
  private final JwtService jwtService;

  public AuthService(AppUserRepo userRepo, OtpService otpService,
                     PasswordEncoder encoder, JwtService jwtService) {
    this.userRepo = userRepo;
    this.otpService = otpService;
    this.encoder = encoder;
    this.jwtService = jwtService;
  }

  @Transactional
  public Dto.RegisterResponse register(Dto.RegisterBody body) {
    String email = body.email.toLowerCase();

    if (userRepo.existsByEmail(email)) {
      throw new AuthException("An account with this email already exists.", 409);
    }

    // Consumes the verified OTP (throws if not verified / expired)
    otpService.consumeVerifiedOtp(email);

    AppUser user = new AppUser();
    user.setEmail(email);
    user.setFirstName(body.firstName);
    user.setLastName(body.lastName);
    user.setPasswordHash(encoder.encode(body.password));
    user.setDob(body.dob);
    user.setRole("BORROWER");
    userRepo.save(user);

    return new Dto.RegisterResponse(user.getId(), user.getStatus());
  }

  public Dto.LoginResponse login(Dto.LoginBody body) {
    AppUser user = userRepo.findByEmail(body.email.toLowerCase())
        .orElseThrow(() -> new AuthException("Invalid email or password.", 401));

    if (!encoder.matches(body.password, user.getPasswordHash())) {
      throw new AuthException("Invalid email or password.", 401);
    }

    String token = jwtService.issue(user);
    return new Dto.LoginResponse(token, jwtService.expirySeconds());
  }

  public Dto.MeResponse me(String bearerToken) {
    String token = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;
    Claims claims = jwtService.parse(token);

    UUID userId = UUID.fromString(claims.getSubject());
    AppUser user = userRepo.findById(userId)
        .orElseThrow(() -> new AuthException("User not found.", 401));

    return new Dto.MeResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
  }
}
