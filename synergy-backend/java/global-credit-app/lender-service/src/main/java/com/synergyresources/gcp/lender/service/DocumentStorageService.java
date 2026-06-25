package com.synergyresources.gcp.lender.service;

import org.springframework.core.io.Resource;

import java.io.IOException;

public interface DocumentStorageService {
  Resource load(String storedPath) throws IOException;
}
