package com.synergyresources.gcp.borrower.service;

import com.synergyresources.gcp.borrower.api.Dto;
import com.synergyresources.gcp.borrower.error.BorrowerException;
import com.synergyresources.gcp.borrower.model.*;
import com.synergyresources.gcp.borrower.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BorrowerApplicationService {

  private final LenderRepo lenderRepo;
  private final LoanApplicationRepo appRepo;
  private final BorrowerProfileRepo profileRepo;
  private final ApplicationDocumentRepo docRepo;
  private final AuditClient auditClient;

  public BorrowerApplicationService(LenderRepo lenderRepo, LoanApplicationRepo appRepo,
                                    BorrowerProfileRepo profileRepo, ApplicationDocumentRepo docRepo,
                                    AuditClient auditClient) {
    this.lenderRepo = lenderRepo;
    this.appRepo = appRepo;
    this.profileRepo = profileRepo;
    this.docRepo = docRepo;
    this.auditClient = auditClient;
  }

  public Dto.ApplyResponse apply(UUID userId, Dto.ApplyRequest req) {
    Lender lender = lenderRepo.findBySlugAndActiveTrue(req.getLenderSlug())
        .orElseThrow(() -> new BorrowerException(404, "Lender not found: " + req.getLenderSlug()));

    LoanApplication app = new LoanApplication();
    app.setUserId(userId);
    app.setLenderId(lender.getId());
    app.setPassportId(req.getPassportId());
    app.setPurpose(req.getPurpose());
    app.setOriginCountry(req.getOriginCountry());
    app.setDestCountry(req.getDestCountry());
    app.setAmount(req.getAmount());
    app.setCurrency(req.getCurrency());
    app.setTermMonths(req.getTermMonths());
    appRepo.save(app);

    auditClient.emit(app.getId(), userId, "BORROWER", "APPLICATION_CREATED",
        "lender=" + req.getLenderSlug());
    return new Dto.ApplyResponse(app.getId(), app.getStatus());
  }

  @Transactional(readOnly = true)
  public Dto.ApplicationResponse get(UUID userId, UUID applicationId) {
    LoanApplication app = requireOwned(userId, applicationId);
    Lender lender = lenderRepo.findById(app.getLenderId())
        .orElseThrow(() -> new BorrowerException(500, "Lender not found"));
    BorrowerProfile profile = profileRepo.findByApplicationId(applicationId).orElse(null);
    List<ApplicationDocument> docs = docRepo.findByApplicationId(applicationId);
    return toResponse(app, lender, profile, docs);
  }

  public Dto.ApplicationResponse updateProfile(UUID userId, UUID applicationId, Dto.ProfileRequest req) {
    LoanApplication app = requireOwned(userId, applicationId);
    BorrowerProfile profile = profileRepo.findByApplicationId(applicationId)
        .orElseGet(() -> {
          BorrowerProfile p = new BorrowerProfile();
          p.setApplicationId(applicationId);
          return p;
        });

    if (req.getFullName() != null) profile.setFullName(req.getFullName());
    if (req.getDob() != null) profile.setDob(req.getDob());
    if (req.getEmail() != null) profile.setEmail(req.getEmail());
    if (req.getPhone() != null) profile.setPhone(req.getPhone());
    if (req.getNationality() != null) profile.setNationality(req.getNationality());
    if (req.getAddressLine() != null) profile.setAddressLine(req.getAddressLine());
    if (req.getCity() != null) profile.setCity(req.getCity());
    if (req.getRegion() != null) profile.setRegion(req.getRegion());
    if (req.getPostalCode() != null) profile.setPostalCode(req.getPostalCode());
    if (req.getCountry() != null) profile.setCountry(req.getCountry());
    if (req.getEmploymentType() != null) profile.setEmploymentType(req.getEmploymentType());
    if (req.getEmployerName() != null) profile.setEmployerName(req.getEmployerName());
    if (req.getMonthlyIncome() != null) profile.setMonthlyIncome(req.getMonthlyIncome());
    if (req.getIncomeCurrency() != null) profile.setIncomeCurrency(req.getIncomeCurrency());
    if (req.getBusinessName() != null) profile.setBusinessName(req.getBusinessName());
    if (req.getBusinessRevenue() != null) profile.setBusinessRevenue(req.getBusinessRevenue());
    if (req.getSupportingRent() != null) profile.setSupportingRent(req.getSupportingRent());
    if (req.getSupportingUtility() != null) profile.setSupportingUtility(req.getSupportingUtility());
    if (req.getSupportingTelecom() != null) profile.setSupportingTelecom(req.getSupportingTelecom());
    profileRepo.save(profile);

    auditClient.emit(applicationId, userId, "BORROWER", "PROFILE_UPDATED", null);

    Lender lender = lenderRepo.findById(app.getLenderId())
        .orElseThrow(() -> new BorrowerException(500, "Lender not found"));
    List<ApplicationDocument> docs = docRepo.findByApplicationId(applicationId);
    return toResponse(app, lender, profile, docs);
  }

  public Dto.SubmitResponse submit(UUID userId, UUID applicationId) {
    LoanApplication app = requireOwned(userId, applicationId);
    if ("SUBMITTED".equals(app.getStatus())) {
      throw new BorrowerException(409, "Application already submitted");
    }
    profileRepo.findByApplicationId(applicationId)
        .orElseThrow(() -> new BorrowerException(400, "Profile required before submission"));
    List<ApplicationDocument> docs = docRepo.findByApplicationId(applicationId);
    if (docs.isEmpty()) {
      throw new BorrowerException(400, "At least one document required before submission");
    }
    app.setStatus("SUBMITTED");
    app.setSubmittedAt(OffsetDateTime.now());
    appRepo.save(app);

    auditClient.emit(applicationId, userId, "BORROWER", "APPLICATION_SUBMITTED", null);
    return new Dto.SubmitResponse(app.getId(), app.getStatus(), app.getSubmittedAt());
  }

  private LoanApplication requireOwned(UUID userId, UUID applicationId) {
    return appRepo.findByIdAndUserId(applicationId, userId)
        .orElseThrow(() -> new BorrowerException(404, "Application not found"));
  }

  private Dto.ApplicationResponse toResponse(LoanApplication app, Lender lender,
                                              BorrowerProfile profile, List<ApplicationDocument> docs) {
    Dto.ApplicationResponse r = new Dto.ApplicationResponse();
    r.setId(app.getId());
    r.setStatus(app.getStatus());
    r.setPurpose(app.getPurpose());
    r.setOriginCountry(app.getOriginCountry());
    r.setDestCountry(app.getDestCountry());
    r.setAmount(app.getAmount());
    r.setCurrency(app.getCurrency());
    r.setTermMonths(app.getTermMonths());
    r.setPassportId(app.getPassportId());
    r.setSubmittedAt(app.getSubmittedAt());
    r.setCreatedAt(app.getCreatedAt());
    r.setUpdatedAt(app.getUpdatedAt());
    r.setLender(new Dto.LenderResponse(lender.getId(), lender.getSlug(), lender.getName(), lender.getBrandColor()));
    if (profile != null) {
      Dto.ProfileResponse pr = new Dto.ProfileResponse();
      pr.setId(profile.getId());
      pr.setFullName(profile.getFullName());
      pr.setDob(profile.getDob());
      pr.setEmail(profile.getEmail());
      pr.setPhone(profile.getPhone());
      pr.setNationality(profile.getNationality());
      pr.setAddressLine(profile.getAddressLine());
      pr.setCity(profile.getCity());
      pr.setRegion(profile.getRegion());
      pr.setPostalCode(profile.getPostalCode());
      pr.setCountry(profile.getCountry());
      pr.setEmploymentType(profile.getEmploymentType());
      pr.setEmployerName(profile.getEmployerName());
      pr.setMonthlyIncome(profile.getMonthlyIncome());
      pr.setIncomeCurrency(profile.getIncomeCurrency());
      pr.setBusinessName(profile.getBusinessName());
      pr.setBusinessRevenue(profile.getBusinessRevenue());
      pr.setSupportingRent(profile.getSupportingRent());
      pr.setSupportingUtility(profile.getSupportingUtility());
      pr.setSupportingTelecom(profile.getSupportingTelecom());
      r.setProfile(pr);
    }
    r.setDocuments(docs.stream().map(d -> new Dto.DocumentResponse(
        d.getId(), d.getDocType(), d.getTag(), d.getOriginalFilename(),
        d.getContentType(), d.getSizeBytes(), d.getCreatedAt()
    )).collect(Collectors.toList()));
    return r;
  }
}
