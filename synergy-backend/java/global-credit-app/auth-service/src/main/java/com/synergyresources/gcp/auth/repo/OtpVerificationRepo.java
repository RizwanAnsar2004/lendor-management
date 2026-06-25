package com.synergyresources.gcp.auth.repo;

import com.synergyresources.gcp.auth.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface OtpVerificationRepo extends JpaRepository<OtpVerification, UUID> {
  Optional<OtpVerification> findTopByEmailOrderByCreatedAtDesc(String email);
  Optional<OtpVerification> findTopByEmailAndVerifiedTrueAndConsumedFalseOrderByCreatedAtDesc(String email);
}
