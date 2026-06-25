package com.synergyresources.gcp.lender.api;

import com.synergyresources.gcp.lender.config.CurrentUser;
import com.synergyresources.gcp.lender.service.LenderReviewService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/lender")
public class LenderMeController {

  private final LenderReviewService reviewService;

  public LenderMeController(LenderReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping("/me")
  public Dto.MeResponse me(
      @RequestHeader(value = "X-User-Id", required = false) String xUserId,
      @RequestHeader(value = "X-User-Role", required = false) String xUserRole) {
    CurrentUser.requireLenderRole(CurrentUser.resolveRole(xUserRole));
    return reviewService.getMe(CurrentUser.resolveId(xUserId));
  }
}
