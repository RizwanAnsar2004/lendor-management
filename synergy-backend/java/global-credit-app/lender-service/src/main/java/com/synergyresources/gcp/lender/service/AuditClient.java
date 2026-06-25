package com.synergyresources.gcp.lender.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synergyresources.gcp.lender.api.Dto;
import com.synergyresources.gcp.lender.error.LenderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class AuditClient {

  private static final Logger log = LoggerFactory.getLogger(AuditClient.class);

  private final RestClient restClient;
  private final ObjectMapper mapper;

  public AuditClient(@Value("${gcp.audit.base-url:http://localhost:8091}") String baseUrl,
                     ObjectMapper mapper) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    this.mapper = mapper;
  }

  public void emit(UUID applicationId, UUID actorUserId, String actorRole,
                   String action, String detail) {
    try {
      Map<String, Object> body = new HashMap<>();
      body.put("service", "lender-service");
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

  public List<Dto.AuditEntry> fetch(UUID applicationId) {
    try {
      String json = restClient.get()
          .uri("/v1/audit/events?applicationId={id}", applicationId)
          .retrieve()
          .body(String.class);

      List<Map<String, Object>> events = mapper.readValue(json, new TypeReference<>() {});
      return events.stream().map(e -> {
        UUID id = e.get("id") != null ? UUID.fromString((String) e.get("id")) : null;
        UUID actorUserId = e.get("actorUserId") != null ? UUID.fromString((String) e.get("actorUserId")) : null;
        String action = (String) e.get("action");
        String detail = (String) e.get("detail");
        OffsetDateTime occurredAt = e.get("occurredAt") != null
            ? OffsetDateTime.parse((String) e.get("occurredAt")) : null;
        return new Dto.AuditEntry(id, actorUserId, action, detail, occurredAt);
      }).toList();
    } catch (LenderException le) {
      throw le;
    } catch (Exception e) {
      log.error("Audit fetch failed for applicationId={}: {}", applicationId, e.getMessage());
      throw new LenderException(502, "Audit service unavailable");
    }
  }
}
