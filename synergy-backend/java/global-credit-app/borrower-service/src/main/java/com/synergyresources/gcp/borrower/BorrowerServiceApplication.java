package com.synergyresources.gcp.borrower;

import com.synergyresources.gcp.borrower.config.BorrowerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BorrowerProperties.class)
public class BorrowerServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BorrowerServiceApplication.class, args);
  }
}
