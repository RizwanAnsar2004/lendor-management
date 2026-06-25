package com.synergyresources.gcp.borrower.api;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public final class Dto {

  private Dto() {}

  public static class ApplyRequest {
    @NotBlank
    private String lenderSlug;
    @NotBlank
    private String purpose;
    private String originCountry;
    private String destCountry;
    @Positive
    private BigDecimal amount;
    private String currency;
    @Min(1)
    private Integer termMonths;
    private UUID passportId;

    public String getLenderSlug() { return lenderSlug; }
    public void setLenderSlug(String lenderSlug) { this.lenderSlug = lenderSlug; }
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
    public UUID getPassportId() { return passportId; }
    public void setPassportId(UUID passportId) { this.passportId = passportId; }
  }

  public static class ApplyResponse {
    private UUID applicationId;
    private String status;

    public ApplyResponse(UUID applicationId, String status) {
      this.applicationId = applicationId;
      this.status = status;
    }

    public UUID getApplicationId() { return applicationId; }
    public String getStatus() { return status; }
  }

  public static class ProfileRequest {
    private String fullName;
    @Past
    private LocalDate dob;
    @Email
    private String email;
    private String phone;
    private String nationality;
    private String addressLine;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private String employmentType;
    private String employerName;
    @DecimalMin("0.0")
    private BigDecimal monthlyIncome;
    private String incomeCurrency;
    private String businessName;
    @DecimalMin("0.0")
    private BigDecimal businessRevenue;
    private Boolean supportingRent;
    private Boolean supportingUtility;
    private Boolean supportingTelecom;

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
  }

  public static class LenderResponse {
    private UUID id;
    private String slug;
    private String name;
    private String brandColor;

    public LenderResponse(UUID id, String slug, String name, String brandColor) {
      this.id = id;
      this.slug = slug;
      this.name = name;
      this.brandColor = brandColor;
    }

    public UUID getId() { return id; }
    public String getSlug() { return slug; }
    public String getName() { return name; }
    public String getBrandColor() { return brandColor; }
  }

  public static class DocumentResponse {
    private UUID id;
    private String docType;
    private String tag;
    private String originalFilename;
    private String contentType;
    private Long sizeBytes;
    private OffsetDateTime createdAt;

    public DocumentResponse(UUID id, String docType, String tag, String originalFilename,
                            String contentType, Long sizeBytes, OffsetDateTime createdAt) {
      this.id = id;
      this.docType = docType;
      this.tag = tag;
      this.originalFilename = originalFilename;
      this.contentType = contentType;
      this.sizeBytes = sizeBytes;
      this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getDocType() { return docType; }
    public String getTag() { return tag; }
    public String getOriginalFilename() { return originalFilename; }
    public String getContentType() { return contentType; }
    public Long getSizeBytes() { return sizeBytes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
  }

  public static class ProfileResponse {
    private UUID id;
    private String fullName;
    private LocalDate dob;
    private String email;
    private String phone;
    private String nationality;
    private String addressLine;
    private String city;
    private String region;
    private String postalCode;
    private String country;
    private String employmentType;
    private String employerName;
    private BigDecimal monthlyIncome;
    private String incomeCurrency;
    private String businessName;
    private BigDecimal businessRevenue;
    private Boolean supportingRent;
    private Boolean supportingUtility;
    private Boolean supportingTelecom;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
  }

  public static class ApplicationResponse {
    private UUID id;
    private String status;
    private String purpose;
    private String originCountry;
    private String destCountry;
    private BigDecimal amount;
    private String currency;
    private Integer termMonths;
    private UUID passportId;
    private LenderResponse lender;
    private ProfileResponse profile;
    private List<DocumentResponse> documents;
    private OffsetDateTime submittedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
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
    public UUID getPassportId() { return passportId; }
    public void setPassportId(UUID passportId) { this.passportId = passportId; }
    public LenderResponse getLender() { return lender; }
    public void setLender(LenderResponse lender) { this.lender = lender; }
    public ProfileResponse getProfile() { return profile; }
    public void setProfile(ProfileResponse profile) { this.profile = profile; }
    public List<DocumentResponse> getDocuments() { return documents; }
    public void setDocuments(List<DocumentResponse> documents) { this.documents = documents; }
    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
  }

  public static class SubmitResponse {
    private UUID applicationId;
    private String status;
    private OffsetDateTime submittedAt;

    public SubmitResponse(UUID applicationId, String status, OffsetDateTime submittedAt) {
      this.applicationId = applicationId;
      this.status = status;
      this.submittedAt = submittedAt;
    }

    public UUID getApplicationId() { return applicationId; }
    public String getStatus() { return status; }
    public OffsetDateTime getSubmittedAt() { return submittedAt; }
  }
}
