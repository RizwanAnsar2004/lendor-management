package com.synergyresources.gcp.audit.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "service", nullable = false, length = 64)
  private String service;

  @Column(name = "application_id")
  private UUID applicationId;

  @Column(name = "actor_user_id")
  private UUID actorUserId;

  @Column(name = "actor_role", length = 32)
  private String actorRole;

  @Column(name = "action", nullable = false, length = 64)
  private String action;

  @Column(name = "detail", columnDefinition = "TEXT")
  private String detail;

  @Column(name = "occurred_at", nullable = false)
  private OffsetDateTime occurredAt;

  @Column(name = "recorded_at", nullable = false, updatable = false)
  private OffsetDateTime recordedAt;

  @PrePersist
  void prePersist() {
    if (id == null) id = UUID.randomUUID();
    if (occurredAt == null) occurredAt = OffsetDateTime.now();
    if (recordedAt == null) recordedAt = OffsetDateTime.now();
  }

  public UUID getId() { return id; }
  public String getService() { return service; }
  public void setService(String service) { this.service = service; }
  public UUID getApplicationId() { return applicationId; }
  public void setApplicationId(UUID applicationId) { this.applicationId = applicationId; }
  public UUID getActorUserId() { return actorUserId; }
  public void setActorUserId(UUID actorUserId) { this.actorUserId = actorUserId; }
  public String getActorRole() { return actorRole; }
  public void setActorRole(String actorRole) { this.actorRole = actorRole; }
  public String getAction() { return action; }
  public void setAction(String action) { this.action = action; }
  public String getDetail() { return detail; }
  public void setDetail(String detail) { this.detail = detail; }
  public OffsetDateTime getOccurredAt() { return occurredAt; }
  public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
  public OffsetDateTime getRecordedAt() { return recordedAt; }
}
