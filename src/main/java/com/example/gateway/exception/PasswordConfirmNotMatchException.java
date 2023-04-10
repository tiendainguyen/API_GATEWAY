package com.example.gateway.exception;

public class PasswordConfirmNotMatchException extends BadRequestException {
  public PasswordConfirmNotMatchException() {
    setCode("org.ptit.okrs.core_authentication.exception.PasswordConfirmNotMatchException");
  }
}
