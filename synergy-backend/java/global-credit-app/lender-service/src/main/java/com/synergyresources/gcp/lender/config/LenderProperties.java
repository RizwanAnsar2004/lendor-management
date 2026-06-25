package com.synergyresources.gcp.lender.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gcp")
public class LenderProperties {

  private Storage storage = new Storage();

  public Storage getStorage() { return storage; }
  public void setStorage(Storage storage) { this.storage = storage; }

  public static class Storage {
    private String localRoot = "./data/uploads";

    public String getLocalRoot() { return localRoot; }
    public void setLocalRoot(String localRoot) { this.localRoot = localRoot; }
  }
}
