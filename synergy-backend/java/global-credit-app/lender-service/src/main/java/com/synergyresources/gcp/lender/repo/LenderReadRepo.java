package com.synergyresources.gcp.lender.repo;

import com.synergyresources.gcp.lender.model.read.LenderRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LenderReadRepo extends JpaRepository<LenderRead, UUID> {
}
