package com.ys.accreditation.app.service.accreditation;

import com.ys.accreditation.app.entity.DocumentRef;
import com.ys.accreditation.app.entity.UserId;
import com.ys.accreditation.app.service.document.DocumentValidationResult;
import com.ys.accreditation.app.service.document.DocumentValidatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class AccreditationServiceTest {

  @Mock DocumentValidatorService documentValidatorService;
  AccreditationService accreditationService;

  @BeforeEach
  void setUp() {
    accreditationService = new AccreditationService(documentValidatorService);
  }

  @Test
  @DisplayName("When all files are valid, should indicate the accreditation is authorized")
  void validateWithAllValidDocuments() {
    Mockito.when(documentValidatorService.validate(Mockito.any(DocumentRef.class)))
        .thenReturn(DocumentValidationResult.builder().valid(true).build());

    DocumentRef doc1 = DocumentRef.builder().build();
    DocumentRef doc2 = DocumentRef.builder().build();

    UserId id = UserId.builder().id("id").build();

    AccreditationProcessResult validate = accreditationService.validate(id, List.of(doc1, doc2));
    assertTrue(validate.isAccredited());
    assertTrue(validate.isSuccess());
  }

  @Test
  @DisplayName("When some file is invalid, should indicate the accreditation as not authorized")
  void validateWithPartiallyValidDocuments() {
    DocumentRef doc1 = DocumentRef.builder().name("valid").build();
    DocumentRef doc2 = DocumentRef.builder().name("invalid").build();

    Mockito.when(documentValidatorService.validate(Mockito.eq(doc1)))
        .thenReturn(DocumentValidationResult.builder().valid(true).build());

    Mockito.when(documentValidatorService.validate(Mockito.eq(doc2)))
        .thenReturn(DocumentValidationResult.builder().valid(false).build());

    UserId id = UserId.builder().id("id").build();

    AccreditationProcessResult validate = accreditationService.validate(id, List.of(doc1, doc2));
    assertFalse(validate.isAccredited());
    assertTrue(validate.isSuccess());
  }

  @Test
  @DisplayName(
      "When there is an error validating files, should indicate the accreditation process was not successful")
  void validateWithFileValidationError() {
    DocumentRef doc1 = DocumentRef.builder().name("valid").build();

    Mockito.when(documentValidatorService.validate(Mockito.eq(doc1)))
        .thenThrow(new RuntimeException("Impossible to validate file"));

    UserId id = UserId.builder().id("id").build();

    AccreditationProcessResult validate = accreditationService.validate(id, List.of(doc1));
    assertFalse(validate.isAccredited());
    assertFalse(validate.isSuccess());
  }
}
