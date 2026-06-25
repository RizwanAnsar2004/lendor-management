package com.synergyresources.gcp.lender.model.read;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "loan_applications")
public class LoanApplicationRead {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "lender_id")
  private UUID lenderId;

  @Column(name = "passport_id")
  private UUID passportId;

  @Column(name = "purpose")
  private String purpose;

  @Column(name = "origin_country")
  private String originCountry;

  @Column(name = "dest_country")
  private String destCountry;

  @Column(name = "amount")
  private BigDecimal amount;

  @Column(name = "currency")
  private String currency;

  @Column(name = "term_months")
  private Integer termMonths;

  @Column(name = "status")
  private String status;

  @Column(name = "submitted_at")
  private OffsetDateTime submittedAt;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
  public UUID getLenderId() { return lenderId; }
  public UUID getPassportId() { return passportId; }
  public String getPurpose() { return purpose; }
  public String getOriginCountry() { return originCountry; }
  public String getDestCountry() { return destCountry; }
  public BigDecimal getAmount() { return amount; }
  public String getCurrency() { return currency; }
  public Integer getTermMonths() { return termMonths; }
  public String getStatus() { return status; }
  public OffsetDateTime getSubmittedAt() { return submittedAt; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
