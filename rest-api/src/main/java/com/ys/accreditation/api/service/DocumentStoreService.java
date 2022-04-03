package com.ys.accreditation.api.service;

import com.ys.accreditation.api.exception.UnknownStorageError;
import com.ys.accreditation.api.model.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class is responsible for store (in the filesystem) the application documents submitted via
 * rest endpoints (in base64 format) before passing the document references to the application
 * module.
 *
 * <p>Since the base64 format choice is directly related to the application endpoints being
 * Rest-based. This class should be responsible for storing and returning a standard Java URI
 * containing the file location (locally or remotely).
 */
@Service
public class DocumentStoreService {

  public static final String FILE_PREFIX = "app_";

  public URI store(Document document) {
    byte[] content = document.getContent();

    try {
      Path path = Files.createTempFile(FILE_PREFIX, document.getName());
      Files.write(path, content);
      return path.toUri();
    } catch (IOException e) {
      throw new UnknownStorageError(e);
    }
  }
}
