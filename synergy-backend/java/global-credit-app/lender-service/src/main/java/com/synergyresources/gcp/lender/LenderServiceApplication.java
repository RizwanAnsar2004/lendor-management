package com.synergyresources.gcp.lender;

import com.synergyresources.gcp.lender.config.LenderProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(LenderProperties.class)
@OpenAPIDefinition(info = @Info(title = "Lender Service", version = "1.0",
    description = "Applicant review, notes, review status, audit log"))
public class LenderServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(LenderServiceApplication.class, args);
  }
}
