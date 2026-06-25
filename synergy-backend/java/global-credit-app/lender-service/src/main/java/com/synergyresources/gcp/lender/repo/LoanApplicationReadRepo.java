package com.synergyresources.gcp.lender.repo;

import com.synergyresources.gcp.lender.model.read.LoanApplicationRead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationReadRepo extends JpaRepository<LoanApplicationRead, UUID> {
  List<LoanApplicationRead> findByLenderIdAndStatus(UUID lenderId, String status);
  Optional<LoanApplicationRead> findByIdAndLenderId(UUID id, UUID lenderId);
}
