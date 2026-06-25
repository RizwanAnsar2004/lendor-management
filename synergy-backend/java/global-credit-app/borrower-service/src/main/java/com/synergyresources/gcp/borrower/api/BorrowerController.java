package com.synergyresources.gcp.borrower.api;

import com.synergyresources.gcp.borrower.config.UserId;
import com.synergyresources.gcp.borrower.service.BorrowerApplicationService;
import com.synergyresources.gcp.borrower.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/applications")
public class BorrowerController {

  private final BorrowerApplicationService appService;
  private final DocumentService documentService;

  public BorrowerController(BorrowerApplicationService appService, DocumentService documentService) {
    this.appService = appService;
    this.documentService = documentService;
  }

  @PostMapping
  public Dto.ApplyResponse apply(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @Valid @RequestBody Dto.ApplyRequest req) {
    return appService.apply(UserId.resolve(xUserId), req);
  }

  @GetMapping("/{id}")
  public Dto.ApplicationResponse get(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @PathVariable UUID id) {
    return appService.get(UserId.resolve(xUserId), id);
  }

  @PutMapping("/{id}/profile")
  public Dto.ApplicationResponse updateProfile(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @PathVariable UUID id,
      @Valid @RequestBody Dto.ProfileRequest req) {
    return appService.updateProfile(UserId.resolve(xUserId), id, req);
  }

  @PostMapping(value = "/{id}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Dto.DocumentResponse uploadDocument(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @PathVariable UUID id,
      @RequestParam("file") MultipartFile file,
      @RequestParam("docType") String docType,
      @RequestParam(value = "tag", required = false) String tag) {
    return documentService.upload(UserId.resolve(xUserId), id, file, docType, tag);
  }

  @GetMapping("/{id}/documents")
  public List<Dto.DocumentResponse> listDocuments(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @PathVariable UUID id) {
    return documentService.list(UserId.resolve(xUserId), id);
  }

  @GetMapping("/{id}/documents/{docId}")
  public ResponseEntity<Resource> downloadDocument(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @PathVariable UUID id,
      @PathVariable UUID docId) {
    DocumentService.DocumentDownload dl = documentService.download(UserId.resolve(xUserId), id, docId);
    String contentType = dl.contentType() != null ? dl.contentType() : "application/octet-stream";
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + dl.filename() + "\"")
        .body(dl.resource());
  }

  @PostMapping("/{id}/submit")
  public Dto.SubmitResponse submit(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @PathVariable UUID id) {
    return appService.submit(UserId.resolve(xUserId), id);
  }
}
