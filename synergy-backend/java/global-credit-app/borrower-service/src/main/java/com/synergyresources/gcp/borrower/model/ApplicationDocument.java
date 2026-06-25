package com.synergyresources.gcp.borrower.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "application_documents")
public class ApplicationDocument {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "application_id", nullable = false)
  private UUID applicationId;

  @Column(name = "doc_type", nullable = false, length = 64)
  private String docType;

  @Column(name = "tag", length = 120)
  private String tag;

  @Column(name = "original_filename", nullable = false, length = 255)
  private String originalFilename;

  @Column(name = "stored_path", nullable = false, length = 512)
  private String storedPath;

  @Column(name = "content_type", length = 120)
  private String contentType;

  @Column(name = "size_bytes")
  private Long sizeBytes;

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
  public UUID getApplicationId() { return applicationId; }
  public void setApplicationId(UUID applicationId) { this.applicationId = applicationId; }
  public String getDocType() { return docType; }
  public void setDocType(String docType) { this.docType = docType; }
  public String getTag() { return tag; }
  public void setTag(String tag) { this.tag = tag; }
  public String getOriginalFilename() { return originalFilename; }
  public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
  public String getStoredPath() { return storedPath; }
  public void setStoredPath(String storedPath) { this.storedPath = storedPath; }
  public String getContentType() { return contentType; }
  public void setContentType(String contentType) { this.contentType = contentType; }
  public Long getSizeBytes() { return sizeBytes; }
  public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
