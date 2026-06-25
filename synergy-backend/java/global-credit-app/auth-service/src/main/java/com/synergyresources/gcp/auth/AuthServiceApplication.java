package com.synergyresources.gcp.auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.synergyresources.gcp.auth.config.AuthProperties;

@SpringBootApplication
@EnableConfigurationProperties(AuthProperties.class)
@OpenAPIDefinition(info = @Info(title = "Auth Service", version = "1.0",
    description = "OTP verification, registration, login, JWT issuance"))
public class AuthServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(AuthServiceApplication.class, args);
  }
}
