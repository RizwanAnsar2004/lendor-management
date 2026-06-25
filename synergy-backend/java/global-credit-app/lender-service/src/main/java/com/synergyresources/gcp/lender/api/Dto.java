package com.synergyresources.gcp.lender.api;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public final class Dto {

  private Dto() {}

  public static class LenderSummary {
    private UUID id;
    private String slug;
    private String name;
    private String brandColor;

    public LenderSummary(UUID id, String slug, String name, String brandColor) {
      this.id = id; this.slug = slug; this.name = name; this.brandColor = brandColor;
    }

    public UUID getId() { return id; }
    public String getSlug() { return slug; }
    public String getName() { return name; }
    public String getBrandColor() { return brandColor; }
  }

  public static class ApplicantSummary {
    private UUID applicationId;
    private String applicantName;
    private String purpose;
    private String corridor;
    private BigDecimal amount;
    private String currency;
    private OffsetDateTime submittedAt;
    private String reviewStatus;
    private Object riskBand = null;

    public UUID getApplicationId() { return applicationId; }
    public void setApplicationId(UUID applicationId) { this.applicationId = applicationId; }
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getCorridor() { return corridor; }
    public void setCorridor(String corridor) { this.corridor = corridor; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }
    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
    public Object getRiskBand() { return riskBand; }
  }

  public static class ProfileDto {
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

  public static class DocumentDto {
    private UUID id;
    private String docType;
    private String tag;
    private String originalFilename;
    private String contentType;
    private Long sizeBytes;
    private OffsetDateTime createdAt;

    public DocumentDto(UUID id, String docType, String tag, String originalFilename,
                       String contentType, Long sizeBytes, OffsetDateTime createdAt) {
      this.id = id; this.docType = docType; this.tag = tag;
      this.originalFilename = originalFilename; this.contentType = contentType;
      this.sizeBytes = sizeBytes; this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getDocType() { return docType; }
    public String getTag() { return tag; }
    public String getOriginalFilename() { return originalFilename; }
    public String getContentType() { return contentType; }
    public Long getSizeBytes() { return sizeBytes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
  }

  public static class ReviewDto {
    private UUID id;
    private String reviewStatus;
    private String recommendation;
    private UUID reviewerUserId;
    private OffsetDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public UUID getReviewerUserId() { return reviewerUserId; }
    public void setReviewerUserId(UUID reviewerUserId) { this.reviewerUserId = reviewerUserId; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
  }

  public static class NoteRequest {
    @NotBlank
    private String body;

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
  }

  public static class NoteResponse {
    private UUID id;
    private UUID authorUserId;
    private String body;
    private OffsetDateTime createdAt;

    public NoteResponse(UUID id, UUID authorUserId, String body, OffsetDateTime createdAt) {
      this.id = id; this.authorUserId = authorUserId; this.body = body; this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getAuthorUserId() { return authorUserId; }
    public String getBody() { return body; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
  }

  public static class ReviewRequest {
    @NotBlank
    private String reviewStatus;
    private String recommendation;

    public String getReviewStatus() { return reviewStatus; }
    public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
  }

  public static class AuditEntry {
    private UUID id;
    private UUID actorUserId;
    private String action;
    private String detail;
    private OffsetDateTime createdAt;

    public AuditEntry(UUID id, UUID actorUserId, String action, String detail, OffsetDateTime createdAt) {
      this.id = id; this.actorUserId = actorUserId; this.action = action;
      this.detail = detail; this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getActorUserId() { return actorUserId; }
    public String getAction() { return action; }
    public String getDetail() { return detail; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
  }

  public static class ApplicationDetail {
    private UUID id;
    private String status;
    private String purpose;
    private String originCountry;
    private String destCountry;
    private BigDecimal amount;
    private String currency;
    private Integer termMonths;
    private OffsetDateTime submittedAt;
    private LenderSummary lender;
    private ProfileDto profile;
    private List<DocumentDto> documents;
    private ReviewDto review;
    private List<NoteResponse> notes;
    private Object risk = null;

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
    public OffsetDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(OffsetDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LenderSummary getLender() { return lender; }
    public void setLender(LenderSummary lender) { this.lender = lender; }
    public ProfileDto getProfile() { return profile; }
    public void setProfile(ProfileDto profile) { this.profile = profile; }
    public List<DocumentDto> getDocuments() { return documents; }
    public void setDocuments(List<DocumentDto> documents) { this.documents = documents; }
    public ReviewDto getReview() { return review; }
    public void setReview(ReviewDto review) { this.review = review; }
    public List<NoteResponse> getNotes() { return notes; }
    public void setNotes(List<NoteResponse> notes) { this.notes = notes; }
    public Object getRisk() { return risk; }
  }

  public static class MeResponse {
    private UUID userId;
    private String role;
    private LenderSummary lender;

    public MeResponse(UUID userId, String role, LenderSummary lender) {
      this.userId = userId; this.role = role; this.lender = lender;
    }

    public UUID getUserId() { return userId; }
    public String getRole() { return role; }
    public LenderSummary getLender() { return lender; }
  }
}
