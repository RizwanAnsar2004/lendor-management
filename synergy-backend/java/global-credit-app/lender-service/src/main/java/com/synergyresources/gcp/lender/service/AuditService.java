package com.synergyresources.gcp.lender.service;

import com.synergyresources.gcp.lender.api.Dto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

  private final AuditClient auditClient;

  public AuditService(AuditClient auditClient) { this.auditClient = auditClient; }

  public void log(UUID applicationId, UUID actorUserId, String action, String detail) {
    auditClient.emit(applicationId, actorUserId, "LENDER", action, detail);
  }

  public List<Dto.AuditEntry> listForApplication(UUID applicationId) {
    return auditClient.fetch(applicationId);
  }
}
