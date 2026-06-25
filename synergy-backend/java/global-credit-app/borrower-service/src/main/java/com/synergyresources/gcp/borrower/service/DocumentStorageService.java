package com.synergyresources.gcp.borrower.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface DocumentStorageService {
  String store(MultipartFile file, UUID userId, UUID applicationId) throws IOException;
  Resource load(String storedPath) throws IOException;
}
