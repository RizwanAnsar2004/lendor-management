package com.synergyresources.gcp.borrower.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lenders")
public class Lender {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "slug", nullable = false, unique = true, length = 64)
  private String slug;

  @Column(name = "name", nullable = false, length = 160)
  private String name;

  @Column(name = "brand_color", length = 16)
  private String brandColor;

  @Column(name = "active", nullable = false)
  private boolean active = true;

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
  public void setId(UUID id) { this.id = id; }
  public String getSlug() { return slug; }
  public void setSlug(String slug) { this.slug = slug; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getBrandColor() { return brandColor; }
  public void setBrandColor(String brandColor) { this.brandColor = brandColor; }
  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
