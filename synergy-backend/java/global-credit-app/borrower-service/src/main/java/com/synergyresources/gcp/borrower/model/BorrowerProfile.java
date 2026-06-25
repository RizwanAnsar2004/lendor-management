package com.synergyresources.gcp.borrower.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "borrower_profiles")
public class BorrowerProfile {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "application_id", nullable = false, unique = true)
  private UUID applicationId;

  @Column(name = "full_name", length = 160)
  private String fullName;

  @Column(name = "dob")
  private LocalDate dob;

  @Column(name = "email", length = 200)
  private String email;

  @Column(name = "phone", length = 32)
  private String phone;

  @Column(name = "nationality", length = 8)
  private String nationality;

  @Column(name = "address_line", length = 255)
  private String addressLine;

  @Column(name = "city", length = 120)
  private String city;

  @Column(name = "region", length = 120)
  private String region;

  @Column(name = "postal_code", length = 32)
  private String postalCode;

  @Column(name = "country", length = 8)
  private String country;

  @Column(name = "employment_type", length = 32)
  private String employmentType;

  @Column(name = "employer_name", length = 160)
  private String employerName;

  @Column(name = "monthly_income", precision = 15, scale = 2)
  private BigDecimal monthlyIncome;

  @Column(name = "income_currency", length = 8)
  private String incomeCurrency;

  @Column(name = "business_name", length = 160)
  private String businessName;

  @Column(name = "business_revenue", precision = 15, scale = 2)
  private BigDecimal businessRevenue;

  @Column(name = "supporting_rent")
  private Boolean supportingRent;

  @Column(name = "supporting_utility")
  private Boolean supportingUtility;

  @Column(name = "supporting_telecom")
  private Boolean supportingTelecom;

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
  public String getFullName() { return fullName; }
  public void setFullName(String fullName) { this.fullName = fullName; }
  public LocalDate getDob() { return dob; }
  public void setDob(LocalDate dob) { this.dob = dob; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }
  public String getNationality() { return nationality; }
  public void setNationality(String nationality) { this.nationality = nationality; }
  public String getAddressLine() { return addressLine; }
  public void setAddressLine(String addressLine) { this.addressLine = addressLine; }
  public String getCity() { return city; }
  public void setCity(String city) { this.city = city; }
  public String getRegion() { return region; }
  public void setRegion(String region) { this.region = region; }
  public String getPostalCode() { return postalCode; }
  public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
  public String getCountry() { return country; }
  public void setCountry(String country) { this.country = country; }
  public String getEmploymentType() { return employmentType; }
  public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
  public String getEmployerName() { return employerName; }
  public void setEmployerName(String employerName) { this.employerName = employerName; }
  public BigDecimal getMonthlyIncome() { return monthlyIncome; }
  public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }
  public String getIncomeCurrency() { return incomeCurrency; }
  public void setIncomeCurrency(String incomeCurrency) { this.incomeCurrency = incomeCurrency; }
  public String getBusinessName() { return businessName; }
  public void setBusinessName(String businessName) { this.businessName = businessName; }
  public BigDecimal getBusinessRevenue() { return businessRevenue; }
  public void setBusinessRevenue(BigDecimal businessRevenue) { this.businessRevenue = businessRevenue; }
  public Boolean getSupportingRent() { return supportingRent; }
  public void setSupportingRent(Boolean supportingRent) { this.supportingRent = supportingRent; }
  public Boolean getSupportingUtility() { return supportingUtility; }
  public void setSupportingUtility(Boolean supportingUtility) { this.supportingUtility = supportingUtility; }
  public Boolean getSupportingTelecom() { return supportingTelecom; }
  public void setSupportingTelecom(Boolean supportingTelecom) { this.supportingTelecom = supportingTelecom; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
