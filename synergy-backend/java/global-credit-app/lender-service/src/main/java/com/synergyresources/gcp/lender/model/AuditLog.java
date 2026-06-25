package com.synergyresources.gcp.lender.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_log")
public class AuditLog {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "application_id")
  private UUID applicationId;

  @Column(name = "actor_user_id")
  private UUID actorUserId;

  @Column(name = "action", nullable = false, length = 64)
  private String action;

  @Column(name = "detail")
  private String detail;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  void prePersist() {
    if (id == null) id = UUID.randomUUID();
    if (createdAt == null) createdAt = OffsetDateTime.now();
  }

  public UUID getId() { return id; }
  public UUID getApplicationId() { return applicationId; }
  public void setApplicationId(UUID applicationId) { this.applicationId = applicationId; }
  public UUID getActorUserId() { return actorUserId; }
  public void setActorUserId(UUID actorUserId) { this.actorUserId = actorUserId; }
  public String getAction() { return action; }
  public void setAction(String action) { this.action = action; }
  public String getDetail() { return detail; }
  public void setDetail(String detail) { this.detail = detail; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
}
