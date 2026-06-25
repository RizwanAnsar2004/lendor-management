package com.synergyresources.gcp.lender.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "application_reviews")
public class ApplicationReview {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "application_id", nullable = false, unique = true)
  private UUID applicationId;

  @Column(name = "lender_id", nullable = false)
  private UUID lenderId;

  @Column(name = "review_status", nullable = false, length = 32)
  private String reviewStatus;

  @Column(name = "recommendation")
  private String recommendation;

  @Column(name = "reviewer_user_id")
  private UUID reviewerUserId;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @PrePersist
  void prePersist() {
    if (id == null) id = UUID.randomUUID();
    if (reviewStatus == null) reviewStatus = "NEW";
    if (createdAt == null) createdAt = OffsetDateTime.now();
    updatedAt = OffsetDateTime.now();
  }

  @PreUpdate
  void preUpdate() { updatedAt = OffsetDateTime.now(); }

  public UUID getId() { return id; }
  public UUID getApplicationId() { return applicationId; }
  public void setApplicationId(UUID applicationId) { this.applicationId = applicationId; }
  public UUID getLenderId() { return lenderId; }
  public void setLenderId(UUID lenderId) { this.lenderId = lenderId; }
  public String getReviewStatus() { return reviewStatus; }
  public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
  public String getRecommendation() { return recommendation; }
  public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
  public UUID getReviewerUserId() { return reviewerUserId; }
  public void setReviewerUserId(UUID reviewerUserId) { this.reviewerUserId = reviewerUserId; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
