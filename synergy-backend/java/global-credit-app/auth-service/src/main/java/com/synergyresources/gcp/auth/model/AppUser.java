package com.synergyresources.gcp.auth.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "app_users")
public class AppUser {

  @Id private UUID id;

  @Column(nullable = false, unique = true) private String email;
  @Column(name = "first_name", nullable = false) private String firstName;
  @Column(name = "last_name", nullable = false) private String lastName;
  @Column(name = "password_hash", nullable = false) private String passwordHash;
  @Column private LocalDate dob;
  @Column(nullable = false) private String status;
  @Column(nullable = false) private String role;
  @Column(name = "created_at", nullable = false) private Instant createdAt;
  @Column(name = "updated_at", nullable = false) private Instant updatedAt;

  @PrePersist void prePersist() {
    var now = Instant.now();
    if (id == null) id = UUID.randomUUID();
    if (createdAt == null) createdAt = now;
    updatedAt = now;
    if (status == null) status = "ACTIVE";
    if (role == null) role = "BORROWER";
  }
  @PreUpdate void preUpdate() { updatedAt = Instant.now(); }

  public UUID getId() { return id; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getFirstName() { return firstName; }
  public void setFirstName(String firstName) { this.firstName = firstName; }
  public String getLastName() { return lastName; }
  public void setLastName(String lastName) { this.lastName = lastName; }
  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
  public LocalDate getDob() { return dob; }
  public void setDob(LocalDate dob) { this.dob = dob; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
}
