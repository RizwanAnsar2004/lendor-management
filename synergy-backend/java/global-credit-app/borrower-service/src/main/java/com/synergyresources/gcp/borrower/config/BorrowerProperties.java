package com.synergyresources.gcp.borrower.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "gcp")
public class BorrowerProperties {

  private Storage storage = new Storage();
  private Documents documents = new Documents();

  public Storage getStorage() { return storage; }
  public void setStorage(Storage storage) { this.storage = storage; }
  public Documents getDocuments() { return documents; }
  public void setDocuments(Documents documents) { this.documents = documents; }

  public static class Storage {
    private String localRoot = "./data/uploads";

    public String getLocalRoot() { return localRoot; }
    public void setLocalRoot(String localRoot) { this.localRoot = localRoot; }
  }

  public static class Documents {
    private String allowedContentTypes = "application/pdf,image/png,image/jpeg";

    public List<String> getAllowedContentTypesList() {
      return Arrays.asList(allowedContentTypes.split(","));
    }

    public String getAllowedContentTypes() { return allowedContentTypes; }
    public void setAllowedContentTypes(String allowedContentTypes) {
      this.allowedContentTypes = allowedContentTypes;
    }
  }
}
