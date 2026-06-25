package com.synergyresources.gcp.borrower.api;

import com.synergyresources.gcp.borrower.error.BorrowerException;
import com.synergyresources.gcp.borrower.model.Lender;
import com.synergyresources.gcp.borrower.repo.LenderRepo;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/lenders")
public class LenderController {

  private final LenderRepo lenderRepo;

  public LenderController(LenderRepo lenderRepo) { this.lenderRepo = lenderRepo; }

  @GetMapping("/{slug}")
  public Dto.LenderResponse getBySlug(@PathVariable String slug) {
    Lender lender = lenderRepo.findBySlugAndActiveTrue(slug)
        .orElseThrow(() -> new BorrowerException(404, "Lender not found: " + slug));
    return new Dto.LenderResponse(lender.getId(), lender.getSlug(), lender.getName(), lender.getBrandColor());
  }
}
