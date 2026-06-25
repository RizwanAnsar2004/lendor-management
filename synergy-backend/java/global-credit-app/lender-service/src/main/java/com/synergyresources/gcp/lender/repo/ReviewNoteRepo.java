package com.synergyresources.gcp.lender.repo;

import com.synergyresources.gcp.lender.model.ReviewNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewNoteRepo extends JpaRepository<ReviewNote, UUID> {
  List<ReviewNote> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
}
