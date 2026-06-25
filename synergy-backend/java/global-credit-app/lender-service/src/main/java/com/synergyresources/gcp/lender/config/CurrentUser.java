package com.synergyresources.gcp.lender.config;

import com.synergyresources.gcp.lender.error.LenderException;

import java.util.UUID;

public final class CurrentUser {

  public static final UUID DEMO_REVIEWER_ID = UUID.fromString("00000000-0000-0000-0000-000000000020");
  private static final String ROLE_LENDER = "LENDER";

  private CurrentUser() {}

  public static UUID resolveId(String xUserId) {
    if (xUserId != null && !xUserId.isBlank()) {
      try { return UUID.fromString(xUserId.trim()); } catch (IllegalArgumentException ignored) {}
    }
    return DEMO_REVIEWER_ID;
  }

  public static String resolveRole(String xUserRole) {
    return (xUserRole != null && !xUserRole.isBlank()) ? xUserRole.trim() : ROLE_LENDER;
  }

  public static void requireLenderRole(String role) {
    if (!ROLE_LENDER.equalsIgnoreCase(role)) {
      throw new LenderException(403, "Lender role required");
    }
  }
}
