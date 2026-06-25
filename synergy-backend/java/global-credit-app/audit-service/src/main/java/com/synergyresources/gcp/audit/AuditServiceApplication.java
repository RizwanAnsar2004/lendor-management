package com.synergyresources.gcp.audit;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Audit Service", version = "1.0",
    description = "Central domain audit trail for all GCP services"))
public class AuditServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(AuditServiceApplication.class, args);
  }
}
