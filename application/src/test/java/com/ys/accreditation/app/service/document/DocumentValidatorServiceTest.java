package com.ys.accreditation.app.service.document;

import com.ys.accreditation.app.entity.DocumentRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentValidatorServiceTest {

  DocumentValidatorService documentValidatorService;

  @BeforeEach
  void setup() {
    documentValidatorService = new DocumentValidatorService();
  }

  @Test
  @DisplayName(
      "When a document with an invalid mime type is provided, should indicate the file as invalid")
  void validateInvalidMimeType() throws URISyntaxException {
    URL resource = DocumentValidatorServiceTest.class.getResource("/logback.xml");
    URI docUri = resource.toURI();

    DocumentRef doc =
        DocumentRef.builder().name("logback.xml").mimeType("application/xml").uri(docUri).build();

    assertFalse(documentValidatorService.validate(doc).isValid());
  }

  @Test
  @DisplayName(
      "When a document with a valid mime type is provided, should indicate the file as valid")
  void validateValidMimeType() throws URISyntaxException {
    URL resource = DocumentValidatorServiceTest.class.getResource("/logback.xml");
    URI docUri = resource.toURI();

    DocumentRef doc =
        DocumentRef.builder().name("logback.pdf").mimeType("application/pdf").uri(docUri).build();

    assertTrue(documentValidatorService.validate(doc).isValid());
  }
}
