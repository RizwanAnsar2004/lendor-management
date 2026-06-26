package com.synergyresources.gcp.passport.api;

import com.synergyresources.gcp.passport.service.PassportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/v1/passports")
public class PassportController {

  private static final UUID DEMO_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  private final PassportService service;

  public PassportController(PassportService service) { this.service = service; }

  private UUID resolveUserId(String xUserId) {
    if (xUserId != null && !xUserId.isBlank()) {
      try { return UUID.fromString(xUserId); } catch (IllegalArgumentException ignored) {}
    }
    return DEMO_USER_ID;
  }

  @PostMapping("/init")
  public Dto.InitResponse init(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @Valid @RequestBody Dto.InitRequest req) {
    return service.init(resolveUserId(xUserId), req);
  }

  @PostMapping("/{passportId}/sources")
  @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
  public void sources(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @PathVariable UUID passportId,
      @Valid @RequestBody Dto.SourceConnectRequest req) {
    service.connectSources(resolveUserId(xUserId), passportId, req);
  }

  @PostMapping("/{passportId}/generate")
  @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.NO_CONTENT)
  public void generate(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @PathVariable UUID passportId) {
    service.generate(resolveUserId(xUserId), passportId);
  }
}
