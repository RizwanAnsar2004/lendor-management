package com.synergyresources.gcp.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AuditClient {

  private static final Logger log = LoggerFactory.getLogger(AuditClient.class);

  private final RestClient restClient;

  public AuditClient(@Value("${gcp.audit.base-url:http://localhost:8091}") String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  public void emit(UUID applicationId, UUID actorUserId, String actorRole,
                   String action, String detail) {
    try {
      Map<String, Object> body = new HashMap<>();
      body.put("service", "auth-service");
      body.put("action", action);
      body.put("occurredAt", OffsetDateTime.now().toString());
      if (applicationId != null) body.put("applicationId", applicationId.toString());
      if (actorUserId != null) body.put("actorUserId", actorUserId.toString());
      if (actorRole != null) body.put("actorRole", actorRole);
      if (detail != null) body.put("detail", detail);

      restClient.post()
          .uri("/v1/audit/events")
          .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
          .body(body)
          .retrieve()
          .toBodilessEntity();
    } catch (Exception e) {
      log.warn("Audit emit failed (action={}): {}", action, e.getMessage());
    }
  }
}
