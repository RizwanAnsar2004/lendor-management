package com.synergyresources.gcp.audit.api;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class Dto {

  private Dto() {}

  public static class EventRequest {
    @NotBlank
    private String service;

    @NotBlank
    private String action;

    private UUID applicationId;
    private UUID actorUserId;
    private String actorRole;
    private String detail;
    private OffsetDateTime occurredAt;

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public UUID getApplicationId() { return applicationId; }
    public void setApplicationId(UUID applicationId) { this.applicationId = applicationId; }
    public UUID getActorUserId() { return actorUserId; }
    public void setActorUserId(UUID actorUserId) { this.actorUserId = actorUserId; }
    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
  }

  public static class EventResponse {
    private UUID id;
    private OffsetDateTime recordedAt;

    public EventResponse(UUID id, OffsetDateTime recordedAt) {
      this.id = id;
      this.recordedAt = recordedAt;
    }

    public UUID getId() { return id; }
    public OffsetDateTime getRecordedAt() { return recordedAt; }
  }

  public static class EventView {
    private UUID id;
    private String service;
    private UUID applicationId;
    private UUID actorUserId;
    private String actorRole;
    private String action;
    private String detail;
    private OffsetDateTime occurredAt;
    private OffsetDateTime recordedAt;

    public EventView(UUID id, String service, UUID applicationId, UUID actorUserId,
                     String actorRole, String action, String detail,
                     OffsetDateTime occurredAt, OffsetDateTime recordedAt) {
      this.id = id; this.service = service; this.applicationId = applicationId;
      this.actorUserId = actorUserId; this.actorRole = actorRole;
      this.action = action; this.detail = detail;
      this.occurredAt = occurredAt; this.recordedAt = recordedAt;
    }

    public UUID getId() { return id; }
    public String getService() { return service; }
    public UUID getApplicationId() { return applicationId; }
    public UUID getActorUserId() { return actorUserId; }
    public String getActorRole() { return actorRole; }
    public String getAction() { return action; }
    public String getDetail() { return detail; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public OffsetDateTime getRecordedAt() { return recordedAt; }
  }
}
