package com.ys.accreditation.api.delegate;

import com.ys.accreditation.api.UserApiDelegate;
import com.ys.accreditation.api.model.AccreditationProofRequest;
import com.ys.accreditation.api.model.AccreditationProofResponse;
import com.ys.accreditation.api.model.Document;
import com.ys.accreditation.api.service.DocumentStoreService;
import com.ys.accreditation.app.entity.DocumentRef;
import com.ys.accreditation.app.entity.UserId;
import com.ys.accreditation.app.service.accreditation.AccreditationProcessResult;
import com.ys.accreditation.app.service.accreditation.AccreditationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserApiDelegateImpl implements UserApiDelegate {

  private final AccreditationService accreditationService;
  private final DocumentStoreService documentStoreService;

  @Override
  public ResponseEntity<AccreditationProofResponse> userAccreditation(
      AccreditationProofRequest accreditationRequest) {
    UserId id = UserId.builder().id(accreditationRequest.getUserId()).build();

    List<DocumentRef> documentRefs =
        accreditationRequest.getPayload().getDocuments().stream()
            .map(this::documentRefConverter)
            .collect(Collectors.toList());

    AccreditationProcessResult accreditationProcessResult =
        accreditationService.validate(id, documentRefs);

    AccreditationProofResponse accreditationProofResponse = new AccreditationProofResponse();
    accreditationProofResponse.setAccredited(accreditationProcessResult.isAccredited());
    accreditationProofResponse.setSuccess(accreditationProcessResult.isSuccess());

    return new ResponseEntity<>(accreditationProofResponse, HttpStatus.OK);
  }

  private DocumentRef documentRefConverter(Document d) {
    return DocumentRef.builder()
        .name(d.getName())
        .mimeType(d.getMimeType())
        .uri(documentStoreService.store(d))
        .build();
  }
}
