package com.synergyresources.gcp.lender.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lender_members")
public class LenderMember {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "user_id", nullable = false, unique = true)
  private UUID userId;

  @Column(name = "lender_id", nullable = false)
  private UUID lenderId;

  @Column(name = "role", nullable = false, length = 32)
  private String role;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @PrePersist
  void prePersist() {
    if (id == null) id = UUID.randomUUID();
    if (createdAt == null) createdAt = OffsetDateTime.now();
    updatedAt = OffsetDateTime.now();
  }

  @PreUpdate
  void preUpdate() { updatedAt = OffsetDateTime.now(); }

  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }
  public UUID getLenderId() { return lenderId; }
  public void setLenderId(UUID lenderId) { this.lenderId = lenderId; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
