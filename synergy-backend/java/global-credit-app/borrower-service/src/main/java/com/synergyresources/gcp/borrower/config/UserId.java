package com.synergyresources.gcp.borrower.config;

import java.util.UUID;

public final class UserId {

  public static final UUID DEMO_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

  private UserId() {}

  public static UUID resolve(String xUserId) {
    if (xUserId != null && !xUserId.isBlank()) {
      try { return UUID.fromString(xUserId.trim()); } catch (IllegalArgumentException ignored) {}
    }
    return DEMO_USER_ID;
  }
}
