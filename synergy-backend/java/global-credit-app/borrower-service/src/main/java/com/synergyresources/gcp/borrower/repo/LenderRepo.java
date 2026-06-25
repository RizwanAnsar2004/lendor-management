package com.synergyresources.gcp.borrower.repo;

import com.synergyresources.gcp.borrower.model.Lender;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LenderRepo extends JpaRepository<Lender, UUID> {
  Optional<Lender> findBySlugAndActiveTrue(String slug);
}
