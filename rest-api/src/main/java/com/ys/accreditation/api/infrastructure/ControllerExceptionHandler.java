package com.ys.accreditation.api.infrastructure;

import com.ys.accreditation.api.model.BadRequestResponse;
import com.ys.accreditation.api.model.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

  private static String validationMessageMapper(FieldError fieldError) {
    return String.format(
        "Invalid field '%s': %s", fieldError.getField(), fieldError.getDefaultMessage());
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<InternalServerError> unhandledException(Exception ex) {
    log.error("Unhandled error.", ex);

    InternalServerError internalServerError =
        new InternalServerError().error(ex.getMessage()).code("InternalServerError");

    return new ResponseEntity<>(internalServerError, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {HttpMessageNotReadableException.class})
  public ResponseEntity<BadRequestResponse> notReadableException(
      HttpMessageNotReadableException ex) {
    log.debug("Unprocessed request content.", ex);

    BadRequestResponse badRequestResponse = new BadRequestResponse();
    badRequestResponse.setErrors(List.of(ex.getMessage()));
    badRequestResponse.code("BadRequest");
    return new ResponseEntity(badRequestResponse, HttpStatus.NOT_ACCEPTABLE);
  }

  @ExceptionHandler(value = {MethodArgumentNotValidException.class})
  protected ResponseEntity<BadRequestResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    List<String> errorList =
        ex.getBindingResult().getFieldErrors().stream()
            .map(ControllerExceptionHandler::validationMessageMapper)
            .collect(Collectors.toList());

    BadRequestResponse badRequestResponse = new BadRequestResponse();
    badRequestResponse.setErrors(errorList);
    badRequestResponse.code("BadRequest");
    return new ResponseEntity(badRequestResponse, HttpStatus.BAD_REQUEST);
  }
}
