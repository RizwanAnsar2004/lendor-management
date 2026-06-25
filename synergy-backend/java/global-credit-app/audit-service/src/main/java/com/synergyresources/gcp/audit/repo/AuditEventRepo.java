package com.synergyresources.gcp.audit.repo;

import com.synergyresources.gcp.audit.model.AuditEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AuditEventRepo extends JpaRepository<AuditEvent, UUID> {

  @Query("select e from AuditEvent e " +
         "where (:applicationId is null or e.applicationId = :applicationId) " +
         "and (:service is null or e.service = :service) " +
         "and (:action is null or e.action = :action) " +
         "order by e.occurredAt desc")
  List<AuditEvent> search(
      @Param("applicationId") UUID applicationId,
      @Param("service") String service,
      @Param("action") String action,
      Pageable page);
}
