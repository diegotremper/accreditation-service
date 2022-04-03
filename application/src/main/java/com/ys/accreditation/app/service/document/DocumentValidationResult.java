package com.ys.accreditation.app.service.document;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class DocumentValidationResult {
  private boolean valid;
}
