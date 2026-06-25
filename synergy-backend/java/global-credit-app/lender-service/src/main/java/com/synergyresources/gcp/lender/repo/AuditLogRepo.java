package com.synergyresources.gcp.lender.repo;

import com.synergyresources.gcp.lender.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepo extends JpaRepository<AuditLog, UUID> {
  List<AuditLog> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
}
