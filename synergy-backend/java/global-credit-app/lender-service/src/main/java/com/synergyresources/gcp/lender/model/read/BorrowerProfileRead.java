package com.synergyresources.gcp.lender.model.read;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Immutable
@Table(name = "borrower_profiles")
public class BorrowerProfileRead {

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "application_id")
  private UUID applicationId;

  @Column(name = "full_name")
  private String fullName;

  @Column(name = "dob")
  private LocalDate dob;

  @Column(name = "email")
  private String email;

  @Column(name = "phone")
  private String phone;

  @Column(name = "nationality")
  private String nationality;

  @Column(name = "address_line")
  private String addressLine;

  @Column(name = "city")
  private String city;

  @Column(name = "region")
  private String region;

  @Column(name = "postal_code")
  private String postalCode;

  @Column(name = "country")
  private String country;

  @Column(name = "employment_type")
  private String employmentType;

  @Column(name = "employer_name")
  private String employerName;

  @Column(name = "monthly_income")
  private BigDecimal monthlyIncome;

  @Column(name = "income_currency")
  private String incomeCurrency;

  @Column(name = "business_name")
  private String businessName;

  @Column(name = "business_revenue")
  private BigDecimal businessRevenue;

  @Column(name = "supporting_rent")
  private Boolean supportingRent;

  @Column(name = "supporting_utility")
  private Boolean supportingUtility;

  @Column(name = "supporting_telecom")
  private Boolean supportingTelecom;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  @Column(name = "updated_at")
  private OffsetDateTime updatedAt;

  public UUID getId() { return id; }
  public UUID getApplicationId() { return applicationId; }
  public String getFullName() { return fullName; }
  public LocalDate getDob() { return dob; }
  public String getEmail() { return email; }
  public String getPhone() { return phone; }
  public String getNationality() { return nationality; }
  public String getAddressLine() { return addressLine; }
  public String getCity() { return city; }
  public String getRegion() { return region; }
  public String getPostalCode() { return postalCode; }
  public String getCountry() { return country; }
  public String getEmploymentType() { return employmentType; }
  public String getEmployerName() { return employerName; }
  public BigDecimal getMonthlyIncome() { return monthlyIncome; }
  public String getIncomeCurrency() { return incomeCurrency; }
  public String getBusinessName() { return businessName; }
  public BigDecimal getBusinessRevenue() { return businessRevenue; }
  public Boolean getSupportingRent() { return supportingRent; }
  public Boolean getSupportingUtility() { return supportingUtility; }
  public Boolean getSupportingTelecom() { return supportingTelecom; }
  public OffsetDateTime getCreatedAt() { return createdAt; }
  public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
