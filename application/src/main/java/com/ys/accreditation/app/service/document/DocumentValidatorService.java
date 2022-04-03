package com.ys.accreditation.app.service.document;

import com.ys.accreditation.app.entity.DocumentRef;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class DocumentValidatorService {

  private static final List<String> ALLOWED_MIME_TYPES = List.of("application/pdf", "image/jpeg");

  public DocumentValidationResult validate(DocumentRef document) {
    log.info("About to validate document: {}", document);

    boolean allowed = ALLOWED_MIME_TYPES.contains(document.getMimeType());

    if (!allowed) {
      log.info("File refused due to invalid mime type");
    }

    log.info("Document is valid? {}", allowed);

    // call sub routine to extract the content of the document
    // and validate it accordingly
    return DocumentValidationResult.builder().valid(allowed).build();
  }
}
