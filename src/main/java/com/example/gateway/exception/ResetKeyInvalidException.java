package com.example.gateway.exception;

public class ResetKeyInvalidException extends BadRequestException {

  public ResetKeyInvalidException() {
    setCode("org.ptit.okrs.core_authentication.exception.ResetKeyInvalidException");
  }
}
