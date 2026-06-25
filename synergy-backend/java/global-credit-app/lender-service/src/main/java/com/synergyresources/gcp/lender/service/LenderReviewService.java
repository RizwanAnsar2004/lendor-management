package com.synergyresources.gcp.lender.service;

import com.synergyresources.gcp.lender.api.Dto;
import com.synergyresources.gcp.lender.error.LenderException;
import com.synergyresources.gcp.lender.model.*;
import com.synergyresources.gcp.lender.model.read.*;
import com.synergyresources.gcp.lender.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LenderReviewService {

  private static final String STATUS_SUBMITTED = "SUBMITTED";
  private static final Set<String> VALID_REVIEW_STATUSES =
      Set.of("NEW", "UNDER_REVIEW", "INFO_REQUESTED", "REVIEWED");

  private final LenderMemberRepo memberRepo;
  private final LoanApplicationReadRepo appRepo;
  private final BorrowerProfileReadRepo profileRepo;
  private final ApplicationDocumentReadRepo docRepo;
  private final LenderReadRepo lenderRepo;
  private final ApplicationReviewRepo reviewRepo;
  private final ReviewNoteRepo noteRepo;
  private final AuditService auditService;

  public LenderReviewService(LenderMemberRepo memberRepo, LoanApplicationReadRepo appRepo,
                              BorrowerProfileReadRepo profileRepo, ApplicationDocumentReadRepo docRepo,
                              LenderReadRepo lenderRepo, ApplicationReviewRepo reviewRepo,
                              ReviewNoteRepo noteRepo, AuditService auditService) {
    this.memberRepo = memberRepo;
    this.appRepo = appRepo;
    this.profileRepo = profileRepo;
    this.docRepo = docRepo;
    this.lenderRepo = lenderRepo;
    this.reviewRepo = reviewRepo;
    this.noteRepo = noteRepo;
    this.auditService = auditService;
  }

  @Transactional(readOnly = true)
  public List<Dto.ApplicantSummary> listApplications(UUID userId) {
    UUID lenderId = resolveLenderId(userId);
    List<LoanApplicationRead> apps = appRepo.findByLenderIdAndStatus(lenderId, STATUS_SUBMITTED);

    return apps.stream().map(app -> {
      BorrowerProfileRead profile = profileRepo.findByApplicationId(app.getId()).orElse(null);
      ApplicationReview review = reviewRepo.findByApplicationId(app.getId()).orElse(null);

      Dto.ApplicantSummary s = new Dto.ApplicantSummary();
      s.setApplicationId(app.getId());
      s.setApplicantName(profile != null ? profile.getFullName() : null);
      s.setPurpose(app.getPurpose());
      s.setCorridor(corridor(app.getOriginCountry(), app.getDestCountry()));
      s.setAmount(app.getAmount());
      s.setCurrency(app.getCurrency());
      s.setSubmittedAt(app.getSubmittedAt());
      s.setReviewStatus(review != null ? review.getReviewStatus() : "NEW");
      return s;
    }).collect(Collectors.toList());
  }

  @Transactional
  public Dto.ApplicationDetail getDetail(UUID userId, UUID applicationId) {
    UUID lenderId = resolveLenderId(userId);
    LoanApplicationRead app = requireOwnedApp(applicationId, lenderId);
    BorrowerProfileRead profile = profileRepo.findByApplicationId(applicationId).orElse(null);
    List<ApplicationDocumentRead> docs = docRepo.findByApplicationId(applicationId);
    ApplicationReview review = reviewRepo.findByApplicationId(applicationId).orElse(null);
    List<ReviewNote> notes = noteRepo.findByApplicationIdOrderByCreatedAtDesc(applicationId);
    LenderRead lender = lenderRepo.findById(lenderId)
        .orElseThrow(() -> new LenderException(500, "Lender not found"));

    auditService.log(applicationId, userId, "VIEW_DETAIL", null);

    Dto.ApplicationDetail detail = new Dto.ApplicationDetail();
    detail.setId(app.getId());
    detail.setStatus(app.getStatus());
    detail.setPurpose(app.getPurpose());
    detail.setOriginCountry(app.getOriginCountry());
    detail.setDestCountry(app.getDestCountry());
    detail.setAmount(app.getAmount());
    detail.setCurrency(app.getCurrency());
    detail.setTermMonths(app.getTermMonths());
    detail.setSubmittedAt(app.getSubmittedAt());
    detail.setLender(new Dto.LenderSummary(lender.getId(), lender.getSlug(), lender.getName(), lender.getBrandColor()));
    detail.setProfile(toProfileDto(profile));
    detail.setDocuments(docs.stream().map(d ->
        new Dto.DocumentDto(d.getId(), d.getDocType(), d.getTag(), d.getOriginalFilename(),
            d.getContentType(), d.getSizeBytes(), d.getCreatedAt())
    ).collect(Collectors.toList()));
    detail.setReview(toReviewDto(review));
    detail.setNotes(notes.stream().map(n ->
        new Dto.NoteResponse(n.getId(), n.getAuthorUserId(), n.getBody(), n.getCreatedAt())
    ).collect(Collectors.toList()));
    return detail;
  }

  public ApplicationDocumentRead requireDocument(UUID userId, UUID applicationId, UUID docId) {
    UUID lenderId = resolveLenderId(userId);
    requireOwnedApp(applicationId, lenderId);
    return docRepo.findByIdAndApplicationId(docId, applicationId)
        .orElseThrow(() -> new LenderException(404, "Document not found"));
  }

  public List<Dto.NoteResponse> getNotes(UUID userId, UUID applicationId) {
    UUID lenderId = resolveLenderId(userId);
    requireOwnedApp(applicationId, lenderId);
    return noteRepo.findByApplicationIdOrderByCreatedAtDesc(applicationId).stream()
        .map(n -> new Dto.NoteResponse(n.getId(), n.getAuthorUserId(), n.getBody(), n.getCreatedAt()))
        .collect(Collectors.toList());
  }

  public Dto.NoteResponse addNote(UUID userId, UUID applicationId, String body) {
    UUID lenderId = resolveLenderId(userId);
    requireOwnedApp(applicationId, lenderId);

    ReviewNote note = new ReviewNote();
    note.setApplicationId(applicationId);
    note.setAuthorUserId(userId);
    note.setBody(body);
    noteRepo.save(note);

    auditService.log(applicationId, userId, "ADD_NOTE", "noteId=" + note.getId());
    return new Dto.NoteResponse(note.getId(), note.getAuthorUserId(), note.getBody(), note.getCreatedAt());
  }

  public Dto.ReviewDto updateReview(UUID userId, UUID applicationId, String reviewStatus, String recommendation) {
    if (!VALID_REVIEW_STATUSES.contains(reviewStatus)) {
      throw new LenderException(400, "Invalid reviewStatus: " + reviewStatus +
          ". Allowed: " + VALID_REVIEW_STATUSES);
    }
    UUID lenderId = resolveLenderId(userId);
    requireOwnedApp(applicationId, lenderId);

    ApplicationReview review = reviewRepo.findByApplicationId(applicationId)
        .orElseGet(() -> {
          ApplicationReview r = new ApplicationReview();
          r.setApplicationId(applicationId);
          r.setLenderId(lenderId);
          return r;
        });
    review.setReviewStatus(reviewStatus);
    review.setRecommendation(recommendation);
    review.setReviewerUserId(userId);
    reviewRepo.save(review);

    auditService.log(applicationId, userId, "UPDATE_REVIEW", "status=" + reviewStatus);
    return toReviewDto(review);
  }

  public List<Dto.AuditEntry> getAudit(UUID userId, UUID applicationId) {
    UUID lenderId = resolveLenderId(userId);
    requireOwnedApp(applicationId, lenderId);
    return auditService.listForApplication(applicationId).stream()
        .map(a -> new Dto.AuditEntry(a.getId(), a.getActorUserId(), a.getAction(), a.getDetail(), a.getCreatedAt()))
        .collect(Collectors.toList());
  }

  public Dto.MeResponse getMe(UUID userId) {
    LenderMember member = memberRepo.findByUserId(userId)
        .orElseThrow(() -> new LenderException(403, "Not a lender member"));
    LenderRead lender = lenderRepo.findById(member.getLenderId())
        .orElseThrow(() -> new LenderException(500, "Lender not found"));
    return new Dto.MeResponse(userId, member.getRole(),
        new Dto.LenderSummary(lender.getId(), lender.getSlug(), lender.getName(), lender.getBrandColor()));
  }

  private UUID resolveLenderId(UUID userId) {
    return memberRepo.findByUserId(userId)
        .orElseThrow(() -> new LenderException(403, "Not a lender member"))
        .getLenderId();
  }

  private LoanApplicationRead requireOwnedApp(UUID applicationId, UUID lenderId) {
    return appRepo.findByIdAndLenderId(applicationId, lenderId)
        .filter(a -> STATUS_SUBMITTED.equals(a.getStatus()))
        .orElseThrow(() -> new LenderException(404, "Application not found"));
  }

  private static String corridor(String origin, String dest) {
    if (origin == null && dest == null) return null;
    return (origin != null ? origin : "?") + "→" + (dest != null ? dest : "?");
  }

  private static Dto.ProfileDto toProfileDto(BorrowerProfileRead p) {
    if (p == null) return null;
    Dto.ProfileDto dto = new Dto.ProfileDto();
    dto.setId(p.getId());
    dto.setFullName(p.getFullName());
    dto.setDob(p.getDob());
    dto.setEmail(p.getEmail());
    dto.setPhone(p.getPhone());
    dto.setNationality(p.getNationality());
    dto.setAddressLine(p.getAddressLine());
    dto.setCity(p.getCity());
    dto.setRegion(p.getRegion());
    dto.setPostalCode(p.getPostalCode());
    dto.setCountry(p.getCountry());
    dto.setEmploymentType(p.getEmploymentType());
    dto.setEmployerName(p.getEmployerName());
    dto.setMonthlyIncome(p.getMonthlyIncome());
    dto.setIncomeCurrency(p.getIncomeCurrency());
    dto.setBusinessName(p.getBusinessName());
    dto.setBusinessRevenue(p.getBusinessRevenue());
    dto.setSupportingRent(p.getSupportingRent());
    dto.setSupportingUtility(p.getSupportingUtility());
    dto.setSupportingTelecom(p.getSupportingTelecom());
    return dto;
  }

  private static Dto.ReviewDto toReviewDto(ApplicationReview r) {
    if (r == null) return null;
    Dto.ReviewDto dto = new Dto.ReviewDto();
    dto.setId(r.getId());
    dto.setReviewStatus(r.getReviewStatus());
    dto.setRecommendation(r.getRecommendation());
    dto.setReviewerUserId(r.getReviewerUserId());
    dto.setUpdatedAt(r.getUpdatedAt());
    return dto;
  }
}
