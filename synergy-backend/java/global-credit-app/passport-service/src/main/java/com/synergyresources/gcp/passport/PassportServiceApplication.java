package com.synergyresources.gcp.passport;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Passport Service", version = "1.0",
    description = "GCP Financial Passport: init, source connect, generate"))
public class PassportServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(PassportServiceApplication.class, args);
  }
}
