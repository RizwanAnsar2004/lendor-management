package com.synergyresources.gcp.borrower.repo;

import com.synergyresources.gcp.borrower.model.ApplicationDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationDocumentRepo extends JpaRepository<ApplicationDocument, UUID> {
  List<ApplicationDocument> findByApplicationId(UUID applicationId);
  Optional<ApplicationDocument> findByIdAndApplicationId(UUID id, UUID applicationId);
}
