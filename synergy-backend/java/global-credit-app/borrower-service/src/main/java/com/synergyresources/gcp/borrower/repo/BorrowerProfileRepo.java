package com.synergyresources.gcp.borrower.repo;

import com.synergyresources.gcp.borrower.model.BorrowerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BorrowerProfileRepo extends JpaRepository<BorrowerProfile, UUID> {
  Optional<BorrowerProfile> findByApplicationId(UUID applicationId);
}
