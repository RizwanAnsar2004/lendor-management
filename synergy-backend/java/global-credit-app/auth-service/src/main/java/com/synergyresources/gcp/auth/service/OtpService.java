package com.synergyresources.gcp.auth.service;

import com.synergyresources.gcp.auth.config.AuthProperties;
import com.synergyresources.gcp.auth.model.OtpVerification;
import com.synergyresources.gcp.auth.repo.OtpVerificationRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;

@Service
public class OtpService {

  private static final SecureRandom RNG = new SecureRandom();

  private final OtpVerificationRepo repo;
  private final PasswordEncoder encoder;
  private final MailService mailService;
  private final AuthProperties props;
  private final AuditClient auditClient;

  public OtpService(OtpVerificationRepo repo, PasswordEncoder encoder,
                    MailService mailService, AuthProperties props, AuditClient auditClient) {
    this.repo = repo;
    this.encoder = encoder;
    this.mailService = mailService;
    this.props = props;
    this.auditClient = auditClient;
  }

  @Transactional
  public void requestOtp(String email) {
    String plain = String.format("%06d", RNG.nextInt(1_000_000));

    OtpVerification otp = new OtpVerification();
    otp.setEmail(email.toLowerCase());
    otp.setCodeHash(encoder.encode(plain));
    otp.setExpiresAt(Instant.now().plusSeconds(props.getOtp().getExpirySeconds()));
    otp.setAttempts(0);
    otp.setVerified(false);
    otp.setConsumed(false);
    repo.save(otp);

    mailService.sendOtp(email, plain);
    auditClient.emit(null, null, null, "OTP_REQUESTED", "email=" + email.toLowerCase());
  }

  @Transactional
  public void verifyOtp(String email, String code) {
    OtpVerification otp = repo.findTopByEmailOrderByCreatedAtDesc(email.toLowerCase())
        .orElseThrow(() -> new AuthException("No OTP found for this email", 400));

    if (otp.isConsumed()) {
      throw new AuthException("OTP already used", 400);
    }
    if (otp.isVerified()) {
      return;
    }
    if (Instant.now().isAfter(otp.getExpiresAt())) {
      throw new AuthException("OTP has expired", 400);
    }
    if (otp.getAttempts() >= props.getOtp().getMaxAttempts()) {
      throw new AuthException("Too many incorrect attempts. Please request a new code.", 400);
    }

    otp.setAttempts(otp.getAttempts() + 1);

    if (!encoder.matches(code, otp.getCodeHash())) {
      repo.save(otp);
      int remaining = props.getOtp().getMaxAttempts() - otp.getAttempts();
      throw new AuthException("Incorrect code. " + remaining + " attempt(s) remaining.", 400);
    }

    otp.setVerified(true);
    repo.save(otp);
    auditClient.emit(null, null, null, "OTP_VERIFIED", "email=" + email.toLowerCase());
  }

  @Transactional
  public OtpVerification consumeVerifiedOtp(String email) {
    OtpVerification otp = repo
        .findTopByEmailAndVerifiedTrueAndConsumedFalseOrderByCreatedAtDesc(email.toLowerCase())
        .orElseThrow(() -> new AuthException("Email not verified. Please complete OTP verification first.", 400));

    if (Instant.now().isAfter(otp.getExpiresAt())) {
      throw new AuthException("Verified OTP session has expired. Please re-verify.", 400);
    }

    otp.setConsumed(true);
    return repo.save(otp);
  }
}
