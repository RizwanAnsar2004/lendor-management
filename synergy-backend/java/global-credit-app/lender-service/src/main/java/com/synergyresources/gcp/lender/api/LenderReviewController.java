package com.synergyresources.gcp.lender.api;

import com.synergyresources.gcp.lender.config.CurrentUser;
import com.synergyresources.gcp.lender.error.LenderException;
import com.synergyresources.gcp.lender.model.read.ApplicationDocumentRead;
import com.synergyresources.gcp.lender.service.AuditService;
import com.synergyresources.gcp.lender.service.DocumentStorageService;
import com.synergyresources.gcp.lender.service.LenderReviewService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/lender/applications")
public class LenderReviewController {

  private final LenderReviewService reviewService;
  private final DocumentStorageService storageService;
  private final AuditService auditService;

  public LenderReviewController(LenderReviewService reviewService,
                                 DocumentStorageService storageService,
                                 AuditService auditService) {
    this.reviewService = reviewService;
    this.storageService = storageService;
    this.auditService = auditService;
  }

  @GetMapping
  public List<Dto.ApplicantSummary> list(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
    CurrentUser.requireLenderRole(CurrentUser.resolveRole(xUserRole));
    return reviewService.listApplications(CurrentUser.resolveId(xUserId));
  }

  @GetMapping("/{id}")
  public Dto.ApplicationDetail detail(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @RequestHeader(value = "X-User-Role", required = false) String xUserRole,
      @PathVariable UUID id) {
    CurrentUser.requireLenderRole(CurrentUser.resolveRole(xUserRole));
    return reviewService.getDetail(CurrentUser.resolveId(xUserId), id);
  }

  @GetMapping("/{id}/documents/{docId}")
  public ResponseEntity<Resource> downloadDocument(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @RequestHeader(value = "X-User-Role", required = false) String xUserRole,
      @PathVariable UUID id,
      @PathVariable UUID docId) {
    CurrentUser.requireLenderRole(CurrentUser.resolveRole(xUserRole));
    UUID userId = CurrentUser.resolveId(xUserId);
    ApplicationDocumentRead doc = reviewService.requireDocument(userId, id, docId);

    Resource resource;
    try {
      resource = storageService.load(doc.getStoredPath());
    } catch (IOException e) {
      throw new LenderException(500, "Failed to load file: " + e.getMessage());
    }

    auditService.log(id, userId, "DOWNLOAD_DOCUMENT", "docId=" + docId);
    String ct = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(ct))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getOriginalFilename() + "\"")
        .body(resource);
  }

  @GetMapping("/{id}/notes")
  public List<Dto.NoteResponse> getNotes(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @RequestHeader(value = "X-User-Role", required = false) String xUserRole,
      @PathVariable UUID id) {
    CurrentUser.requireLenderRole(CurrentUser.resolveRole(xUserRole));
    return reviewService.getNotes(CurrentUser.resolveId(xUserId), id);
  }

  @PostMapping("/{id}/notes")
  public Dto.NoteResponse addNote(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @RequestHeader(value = "X-User-Role", required = false) String xUserRole,
      @PathVariable UUID id,
      @Valid @RequestBody Dto.NoteRequest req) {
    CurrentUser.requireLenderRole(CurrentUser.resolveRole(xUserRole));
    return reviewService.addNote(CurrentUser.resolveId(xUserId), id, req.getBody());
  }

  @PutMapping("/{id}/review")
  public Dto.ReviewDto updateReview(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @RequestHeader(value = "X-User-Role", required = false) String xUserRole,
      @PathVariable UUID id,
      @Valid @RequestBody Dto.ReviewRequest req) {
    CurrentUser.requireLenderRole(CurrentUser.resolveRole(xUserRole));
    return reviewService.updateReview(CurrentUser.resolveId(xUserId), id,
        req.getReviewStatus(), req.getRecommendation());
  }

  @GetMapping("/{id}/audit")
  public List<Dto.AuditEntry> getAudit(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @RequestHeader(value = "X-User-Role", required = false) String xUserRole,
      @PathVariable UUID id) {
    CurrentUser.requireLenderRole(CurrentUser.resolveRole(xUserRole));
    return reviewService.getAudit(CurrentUser.resolveId(xUserId), id);
  }
}
