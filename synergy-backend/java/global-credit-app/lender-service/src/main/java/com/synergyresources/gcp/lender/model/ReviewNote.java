package com.synergyresources.gcp.lender.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "review_notes")
public class ReviewNote {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "application_id", nullable = false)
  private UUID applicationId;

  @Column(name = "author_user_id", nullable = false)
  private UUID authorUserId;

  @Column(name = "body", nullable = false)
  private String body;

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
  public UUID getAuthorUserId() { return authorUserId; }
  public void setAuthorUserId(UUID authorUserId) { this.authorUserId = authorUserId; }
  public String getBody() { return body; }
  public void setBody(String body) { this.body = body; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
}
