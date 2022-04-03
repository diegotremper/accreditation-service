package com.ys.accreditation.app.service.accreditation;

import com.ys.accreditation.app.entity.DocumentRef;
import com.ys.accreditation.app.entity.UserId;
import com.ys.accreditation.app.service.document.DocumentValidatorService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@AllArgsConstructor
public class AccreditationService {

  private final DocumentValidatorService documentValidatorService;

  public AccreditationProcessResult validate(UserId userId, List<DocumentRef> documents) {
    log.info("About processing request: {}", userId);

    AccreditationProcessResult.AccreditationProcessResultBuilder builder =
        AccreditationProcessResult.builder();

    try {
      boolean allDocumentsAreValid =
          documents.stream()
              .allMatch(document -> documentValidatorService.validate(document).isValid());
      builder.accredited(allDocumentsAreValid);
      builder.success(true);
    } catch (Exception e) {
      // in any case of an unexpected errors, just log the exception and mark the
      // response with no success
      log.error("There is an error trying to process the accreditation for the user: {}", userId, e);
      builder.success(false);
    }

    AccreditationProcessResult accreditationProcessResult = builder.build();

    log.info("Accreditation processing results: {} {}", userId, accreditationProcessResult);

    return accreditationProcessResult;
  }
}
