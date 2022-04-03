package com.ys.accreditation.app.service.accreditation;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class AccreditationProcessResult {
  private boolean accredited;
  private boolean success;
}
