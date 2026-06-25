package com.synergyresources.gcp.lender.repo;

import com.synergyresources.gcp.lender.model.read.BorrowerProfileRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BorrowerProfileReadRepo extends JpaRepository<BorrowerProfileRead, UUID> {
  Optional<BorrowerProfileRead> findByApplicationId(UUID applicationId);
}
