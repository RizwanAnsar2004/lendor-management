package com.synergyresources.gcp.audit.service;

import com.synergyresources.gcp.audit.api.Dto;
import com.synergyresources.gcp.audit.model.AuditEvent;
import com.synergyresources.gcp.audit.repo.AuditEventRepo;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuditEventService {

  private static final int MAX_LIMIT = 500;

  private final AuditEventRepo repo;

  public AuditEventService(AuditEventRepo repo) { this.repo = repo; }

  @Transactional
  public Dto.EventResponse record(Dto.EventRequest req) {
    AuditEvent event = new AuditEvent();
    event.setService(req.getService());
    event.setApplicationId(req.getApplicationId());
    event.setActorUserId(req.getActorUserId());
    event.setActorRole(req.getActorRole());
    event.setAction(req.getAction());
    event.setDetail(req.getDetail());
    event.setOccurredAt(req.getOccurredAt());
    repo.save(event);
    return new Dto.EventResponse(event.getId(), event.getRecordedAt());
  }

  @Transactional(readOnly = true)
  public List<Dto.EventView> search(UUID applicationId, String service, String action, int limit) {
    int cap = Math.min(limit, MAX_LIMIT);
    return repo.search(applicationId, service, action, PageRequest.ofSize(cap))
        .stream()
        .map(e -> new Dto.EventView(e.getId(), e.getService(), e.getApplicationId(),
            e.getActorUserId(), e.getActorRole(), e.getAction(), e.getDetail(),
            e.getOccurredAt(), e.getRecordedAt()))
        .collect(Collectors.toList());
  }
}
