package com.ys.accreditation.app.entity;

import lombok.Builder;
import lombok.Value;

import java.net.URI;

/** Represents a document reference used by the application to validate the user accreditation. */
@Builder
@Value
public class DocumentRef {
  private String name;
  private String mimeType;
  private URI uri;
}
