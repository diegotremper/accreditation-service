package com.ys.accreditation.api.delegate;

import com.ys.accreditation.api.model.AccreditationPayload;
import com.ys.accreditation.api.model.AccreditationProofRequest;
import com.ys.accreditation.api.model.AccreditationProofResponse;
import com.ys.accreditation.api.model.Document;
import com.ys.accreditation.api.service.DocumentStoreService;
import com.ys.accreditation.app.entity.UserId;
import com.ys.accreditation.app.service.accreditation.AccreditationProcessResult;
import com.ys.accreditation.app.service.accreditation.AccreditationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserApiDelegateImplTest {

  @Mock AccreditationService accreditationService;
  @Mock DocumentStoreService documentStoreService;
  UserApiDelegateImpl userApiDelegate;

  @BeforeEach
  void setup() {
    userApiDelegate = new UserApiDelegateImpl(accreditationService, documentStoreService);
  }

  @Test
  @DisplayName(
      "When a invalid accreditation type is provided, should return a not acceptable response")
  void userAccreditationPost() {
    String userId = "userId";

    UserId expectedId = UserId.builder().id(userId).build();
    AccreditationProcessResult expectedAccreditationProcessResult =
        AccreditationProcessResult.builder().accredited(true).build();

    when(accreditationService.validate(eq(expectedId), anyList()))
        .thenReturn(expectedAccreditationProcessResult);

    List<Document> documentList =
        List.of(
            new Document()
                .name("test.pdf")
                .mimeType("application/pdf")
                .content(
                    Base64.getEncoder().encode("some-content".getBytes(StandardCharsets.UTF_8))));

    AccreditationProofRequest accreditationProofRequest =
        new AccreditationProofRequest()
            .userId(userId)
            .payload(
                new AccreditationPayload()
                    .accreditationType(AccreditationPayload.AccreditationTypeEnum.BY_INCOME)
                    .documents(documentList));

    ResponseEntity<AccreditationProofResponse> response =
        userApiDelegate.userAccreditation(accreditationProofRequest);

    assertNotNull(response);
    assertTrue(response.getBody().getAccredited());
    verify(accreditationService).validate(eq(expectedId), anyList());
  }
}
