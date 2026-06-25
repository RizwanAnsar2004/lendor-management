package com.synergyresources.gcp.auth.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "otp_verification")
public class OtpVerification {

  @Id private UUID id;

  @Column(nullable = false) private String email;
  @Column(name = "code_hash", nullable = false) private String codeHash;
  @Column(name = "expires_at", nullable = false) private Instant expiresAt;
  @Column(nullable = false) private int attempts;
  @Column(nullable = false) private boolean verified;
  @Column(nullable = false) private boolean consumed;
  @Column(name = "created_at", nullable = false) private Instant createdAt;
  @Column(name = "updated_at", nullable = false) private Instant updatedAt;

  @PrePersist void prePersist() {
    var now = Instant.now();
    if (id == null) id = UUID.randomUUID();
    if (createdAt == null) createdAt = now;
    updatedAt = now;
  }
  @PreUpdate void preUpdate() { updatedAt = Instant.now(); }

  public UUID getId() { return id; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getCodeHash() { return codeHash; }
  public void setCodeHash(String codeHash) { this.codeHash = codeHash; }
  public Instant getExpiresAt() { return expiresAt; }
  public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
  public int getAttempts() { return attempts; }
  public void setAttempts(int attempts) { this.attempts = attempts; }
  public boolean isVerified() { return verified; }
  public void setVerified(boolean verified) { this.verified = verified; }
  public boolean isConsumed() { return consumed; }
  public void setConsumed(boolean consumed) { this.consumed = consumed; }
}
