package com.synergyresources.gcp.lender.repo;

import com.synergyresources.gcp.lender.model.ApplicationReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationReviewRepo extends JpaRepository<ApplicationReview, UUID> {
  Optional<ApplicationReview> findByApplicationId(UUID applicationId);
}
