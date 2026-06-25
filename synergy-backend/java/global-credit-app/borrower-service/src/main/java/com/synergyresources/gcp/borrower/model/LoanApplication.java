package com.synergyresources.gcp.borrower.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "lender_id", nullable = false)
  private UUID lenderId;

  @Column(name = "passport_id")
  private UUID passportId;

  @Column(name = "purpose", nullable = false, length = 64)
  private String purpose;

  @Column(name = "origin_country", length = 8)
  private String originCountry;

  @Column(name = "dest_country", length = 8)
  private String destCountry;

  @Column(name = "amount", precision = 15, scale = 2)
  private BigDecimal amount;

  @Column(name = "currency", length = 8)
  private String currency;

  @Column(name = "term_months")
  private Integer termMonths;

  @Column(name = "status", nullable = false, length = 32)
  private String status;

  @Column(name = "submitted_at")
  private OffsetDateTime submittedAt;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @PrePersist
  void prePersist() {
    if (id == null) id = UUID.randomUUID();
    if (status == null) status = "DRAFT";
    if (createdAt == null) createdAt = OffsetDateTime.now();
    updatedAt = OffsetDateTime.now();
  }

  @PreUpdate
  void preUpdate() { updatedAt = OffsetDateTime.now(); }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }
  public UUID getLenderId() { return lenderId; }
  public void setLenderId(UUID lenderId) { this.lenderId = lenderId; }
  public UUID getPassportId() { return passportId; }
  public void setPassportId(UUID passportId) { this.passportId = passportId; }
  public String getPurpose() { return purpose; }
  public void setPurpose(String purpose) { this.purpose = purpose; }
  public String getOriginCountry() { return originCountry; }
  public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }
  public String getDestCountry() { return destCountry; }
  public void setDestCountry(String destCountry) { this.destCountry = destCountry; }
  public BigDecimal getAmount() { return amount; }
  public void setAmount(BigDecimal amount) { this.amount = amount; }
  public String getCurrency() { return currency; }
  public void setCurrency(String currency) { this.currency = currency; }
  public Integer getTermMonths() { return termMonths; }
  public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public OffsetDateTime getSubmittedAt() { return submittedAt; }
  public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
