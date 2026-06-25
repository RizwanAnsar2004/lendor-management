package com.synergyresources.gcp.audit.api;

import com.synergyresources.gcp.audit.service.AuditEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/audit/events")
public class AuditController {

  private final AuditEventService service;

  public AuditController(AuditEventService service) { this.service = service; }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Dto.EventResponse record(@Valid @RequestBody Dto.EventRequest req) {
    return service.record(req);
  }

  @GetMapping
  public List<Dto.EventView> query(
      @RequestParam(required = false) UUID applicationId,
      @RequestParam(name = "service", required = false) String serviceFilter,
      @RequestParam(required = false) String action,
      @RequestParam(defaultValue = "100") int limit) {
    return service.search(applicationId, serviceFilter, action, limit);
  }
}
