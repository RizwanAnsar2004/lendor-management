package com.synergyresources.gcp.borrower.repo;

import com.synergyresources.gcp.borrower.model.LoanApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationRepo extends JpaRepository<LoanApplication, UUID> {
  Optional<LoanApplication> findByIdAndUserId(UUID id, UUID userId);
}
