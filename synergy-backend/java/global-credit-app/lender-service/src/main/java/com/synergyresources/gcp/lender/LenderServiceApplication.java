package com.synergyresources.gcp.lender;

import com.synergyresources.gcp.lender.config.LenderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(LenderProperties.class)
public class LenderServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(LenderServiceApplication.class, args);
  }
}
