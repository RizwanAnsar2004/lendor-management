package com.synergyresources.gcp.borrower.service;

import com.synergyresources.gcp.borrower.config.BorrowerProperties;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalDocumentStorageService implements DocumentStorageService {

  private final String localRoot;

  public LocalDocumentStorageService(BorrowerProperties props) {
    this.localRoot = props.getStorage().getLocalRoot();
  }

  @Override
  public String store(MultipartFile file, UUID userId, UUID applicationId) throws IOException {
    String safeFilename = Paths.get(file.getOriginalFilename()).getFileName().toString()
        .replaceAll("[^a-zA-Z0-9._-]", "_");
    String key = "documents/" + userId + "/" + applicationId + "/" + UUID.randomUUID() + "_" + safeFilename;
    Path target = Paths.get(localRoot, key).toAbsolutePath().normalize();
    Files.createDirectories(target.getParent());
    file.transferTo(target.toFile());
    return key;
  }

  @Override
  public Resource load(String storedPath) throws IOException {
    Path path = Paths.get(localRoot, storedPath).toAbsolutePath().normalize();
    if (!Files.exists(path)) {
      throw new IOException("File not found: " + storedPath);
    }
    return new FileSystemResource(path);
  }
}
