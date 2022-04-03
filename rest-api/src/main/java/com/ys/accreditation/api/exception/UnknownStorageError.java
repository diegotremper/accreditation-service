package com.ys.accreditation.api.exception;

import java.io.IOException;

public class UnknownStorageError extends RuntimeException {
  public UnknownStorageError(IOException e) {
    super(e);
  }
}
