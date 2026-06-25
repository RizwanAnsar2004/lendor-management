package com.synergyresources.gcp.borrower.service;

import com.synergyresources.gcp.borrower.api.Dto;
import com.synergyresources.gcp.borrower.config.BorrowerProperties;
import com.synergyresources.gcp.borrower.error.BorrowerException;
import com.synergyresources.gcp.borrower.model.ApplicationDocument;
import com.synergyresources.gcp.borrower.repo.ApplicationDocumentRepo;
import com.synergyresources.gcp.borrower.repo.LoanApplicationRepo;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocumentService {

  private final LoanApplicationRepo appRepo;
  private final ApplicationDocumentRepo docRepo;
  private final DocumentStorageService storageService;
  private final List<String> allowedTypes;

  private static final long MAX_BYTES = 10L * 1024 * 1024;

  public DocumentService(LoanApplicationRepo appRepo, ApplicationDocumentRepo docRepo,
                         DocumentStorageService storageService, BorrowerProperties props) {
    this.appRepo = appRepo;
    this.docRepo = docRepo;
    this.storageService = storageService;
    this.allowedTypes = props.getDocuments().getAllowedContentTypesList();
  }

  public Dto.DocumentResponse upload(UUID userId, UUID applicationId, MultipartFile file,
                                     String docType, String tag) {
    requireOwned(userId, applicationId);

    String contentType = file.getContentType();
    if (contentType == null || !allowedTypes.contains(contentType)) {
      throw new BorrowerException(400, "File type not allowed: " + contentType);
    }
    if (file.getSize() > MAX_BYTES) {
      throw new BorrowerException(400, "File exceeds 10 MB limit");
    }

    String storedPath;
    try {
      storedPath = storageService.store(file, userId, applicationId);
    } catch (IOException e) {
      throw new BorrowerException(500, "Failed to store file: " + e.getMessage());
    }

    ApplicationDocument doc = new ApplicationDocument();
    doc.setApplicationId(applicationId);
    doc.setDocType(docType);
    doc.setTag(tag);
    doc.setOriginalFilename(file.getOriginalFilename());
    doc.setStoredPath(storedPath);
    doc.setContentType(contentType);
    doc.setSizeBytes(file.getSize());
    docRepo.save(doc);

    return new Dto.DocumentResponse(doc.getId(), doc.getDocType(), doc.getTag(),
        doc.getOriginalFilename(), doc.getContentType(), doc.getSizeBytes(), doc.getCreatedAt());
  }

  @Transactional(readOnly = true)
  public List<Dto.DocumentResponse> list(UUID userId, UUID applicationId) {
    requireOwned(userId, applicationId);
    return docRepo.findByApplicationId(applicationId).stream()
        .map(d -> new Dto.DocumentResponse(d.getId(), d.getDocType(), d.getTag(),
            d.getOriginalFilename(), d.getContentType(), d.getSizeBytes(), d.getCreatedAt()))
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public DocumentDownload download(UUID userId, UUID applicationId, UUID docId) {
    requireOwned(userId, applicationId);
    ApplicationDocument doc = docRepo.findByIdAndApplicationId(docId, applicationId)
        .orElseThrow(() -> new BorrowerException(404, "Document not found"));
    Resource resource;
    try {
      resource = storageService.load(doc.getStoredPath());
    } catch (IOException e) {
      throw new BorrowerException(500, "Failed to load file: " + e.getMessage());
    }
    return new DocumentDownload(resource, doc.getOriginalFilename(), doc.getContentType());
  }

  private void requireOwned(UUID userId, UUID applicationId) {
    appRepo.findByIdAndUserId(applicationId, userId)
        .orElseThrow(() -> new BorrowerException(404, "Application not found"));
  }

  public record DocumentDownload(Resource resource, String filename, String contentType) {}
}
