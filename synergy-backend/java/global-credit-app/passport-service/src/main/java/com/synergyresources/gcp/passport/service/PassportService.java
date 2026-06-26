package com.synergyresources.gcp.passport.service;

import com.synergyresources.gcp.passport.api.Dto;
import com.synergyresources.gcp.passport.error.PassportException;
import com.synergyresources.gcp.passport.model.Passport;
import com.synergyresources.gcp.passport.model.PassportSource;
import com.synergyresources.gcp.passport.repo.PassportRepo;
import com.synergyresources.gcp.passport.repo.PassportSourceRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class PassportService {
  private final PassportRepo passportRepo;
  private final PassportSourceRepo sourceRepo;
  private final AuditClient auditClient;

  public PassportService(PassportRepo passportRepo, PassportSourceRepo sourceRepo,
                         AuditClient auditClient) {
    this.passportRepo = passportRepo;
    this.sourceRepo = sourceRepo;
    this.auditClient = auditClient;
  }

  @Transactional
  public Dto.InitResponse init(UUID userId, Dto.InitRequest req) {
    Passport p = new Passport();
    p.setUserId(userId);
    p.setPurpose(req.purpose);
    p.setOriginCountry(req.originCountry);
    p.setDestCountry(req.destCountry);
    p.setFullName(req.fullName);
    p.setDob(req.dob);
    p.setStatus("IN_PROGRESS");
    passportRepo.save(p);

    auditClient.emit(p.getId(), userId, "BORROWER", "PASSPORT_INIT", null);
    return new Dto.InitResponse(p.getId(), p.getStatus());
  }

  @Transactional
  public void connectSources(UUID userId, UUID passportId, Dto.SourceConnectRequest req) {
    Passport p = passportRepo.findByIdAndUserId(passportId, userId)
      .orElseThrow(() -> new PassportException(404, "Passport not found"));

    for (String s : req.sources) {
      PassportSource ps = new PassportSource();
      ps.setPassportId(p.getId());
      ps.setSourceType(s);
      ps.setConnected(true);
      sourceRepo.save(ps);
    }
    p.setStatus("IN_PROGRESS");
    passportRepo.save(p);

    auditClient.emit(passportId, userId, "BORROWER", "SOURCES_CONNECTED",
        "count=" + req.sources.size());
  }

  @Transactional
  public void generate(UUID userId, UUID passportId) {
    Passport p = passportRepo.findByIdAndUserId(passportId, userId)
      .orElseThrow(() -> new PassportException(404, "Passport not found"));
    p.setStatus("ACTIVE");
    passportRepo.save(p);

    auditClient.emit(passportId, userId, "BORROWER", "PASSPORT_GENERATED", null);
  }
}
