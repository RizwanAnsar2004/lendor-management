package com.synergyresources.gcp.lender.repo;

import com.synergyresources.gcp.lender.model.LenderMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LenderMemberRepo extends JpaRepository<LenderMember, UUID> {
  Optional<LenderMember> findByUserId(UUID userId);
}
