package com.synergyresources.gcp.borrower;

import com.synergyresources.gcp.borrower.config.BorrowerProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BorrowerProperties.class)
@OpenAPIDefinition(info = @Info(title = "Borrower Service", version = "1.0",
    description = "Loan applications, borrower profiles, and document uploads"))
public class BorrowerServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BorrowerServiceApplication.class, args);
  }
}
