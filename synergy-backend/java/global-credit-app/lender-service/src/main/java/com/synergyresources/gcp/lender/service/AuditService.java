package com.synergyresources.gcp.lender.service;

import com.synergyresources.gcp.lender.model.AuditLog;
import com.synergyresources.gcp.lender.repo.AuditLogRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AuditService {

  private final AuditLogRepo repo;

  public AuditService(AuditLogRepo repo) { this.repo = repo; }

  @Transactional
  public void log(UUID applicationId, UUID actorUserId, String action, String detail) {
    AuditLog entry = new AuditLog();
    entry.setApplicationId(applicationId);
    entry.setActorUserId(actorUserId);
    entry.setAction(action);
    entry.setDetail(detail);
    repo.save(entry);
  }

  @Transactional(readOnly = true)
  public List<AuditLog> listForApplication(UUID applicationId) {
    return repo.findByApplicationIdOrderByCreatedAtDesc(applicationId);
  }
}
