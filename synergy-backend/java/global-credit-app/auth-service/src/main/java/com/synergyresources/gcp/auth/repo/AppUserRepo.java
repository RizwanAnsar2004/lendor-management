package com.synergyresources.gcp.auth.repo;

import com.synergyresources.gcp.auth.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepo extends JpaRepository<AppUser, UUID> {
  Optional<AppUser> findByEmail(String email);
  boolean existsByEmail(String email);
}
