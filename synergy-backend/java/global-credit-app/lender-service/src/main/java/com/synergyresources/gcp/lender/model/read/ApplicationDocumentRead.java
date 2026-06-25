package com.synergyresources.gcp.lender.model.read;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "application_documents")
public class ApplicationDocumentRead {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "application_id")
  private UUID applicationId;

  @Column(name = "doc_type")
  private String docType;

  @Column(name = "tag")
  private String tag;

  @Column(name = "original_filename")
  private String originalFilename;

  @Column(name = "stored_path")
  private String storedPath;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "size_bytes")
  private Long sizeBytes;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  public UUID getId() { return id; }
  public UUID getApplicationId() { return applicationId; }
  public String getDocType() { return docType; }
  public String getTag() { return tag; }
  public String getOriginalFilename() { return originalFilename; }
  public String getStoredPath() { return storedPath; }
  public String getContentType() { return contentType; }
  public Long getSizeBytes() { return sizeBytes; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
}
