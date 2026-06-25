package com.synergyresources.gcp.lender.repo;

import com.synergyresources.gcp.lender.model.read.ApplicationDocumentRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationDocumentReadRepo extends JpaRepository<ApplicationDocumentRead, UUID> {
  List<ApplicationDocumentRead> findByApplicationId(UUID applicationId);
  Optional<ApplicationDocumentRead> findByIdAndApplicationId(UUID id, UUID applicationId);
}
