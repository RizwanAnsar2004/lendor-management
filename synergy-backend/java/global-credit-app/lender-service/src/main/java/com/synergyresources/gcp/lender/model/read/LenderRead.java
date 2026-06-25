package com.synergyresources.gcp.lender.model.read;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@Immutable
@Table(name = "lenders")
public class LenderRead {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "slug")
  private String slug;

  @Column(name = "name")
  private String name;

  @Column(name = "brand_color")
  private String brandColor;

  @Column(name = "active")
  private boolean active;

  public UUID getId() { return id; }
  public String getSlug() { return slug; }
  public String getName() { return name; }
  public String getBrandColor() { return brandColor; }
  public boolean isActive() { return active; }
}
