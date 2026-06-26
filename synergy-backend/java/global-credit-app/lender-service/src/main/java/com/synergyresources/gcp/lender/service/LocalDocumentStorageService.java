package com.synergyresources.gcp.lender.service;

import com.synergyresources.gcp.lender.config.LenderProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class LocalDocumentStorageService implements DocumentStorageService {

  private final String localRoot;

  public LocalDocumentStorageService(LenderProperties props) {
    this.localRoot = props.getStorage().getLocalRoot();
  }

  @Override
  public Resource load(String storedPath) throws IOException {
    var path = Paths.get(localRoot, storedPath).toAbsolutePath().normalize();
    if (!Files.exists(path)) throw new IOException("File not found: " + storedPath);
    return new FileSystemResource(path);
  }
}
