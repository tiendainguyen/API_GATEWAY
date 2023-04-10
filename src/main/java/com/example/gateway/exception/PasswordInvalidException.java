package com.example.gateway.exception;


public class PasswordInvalidException extends BadRequestException {

  public PasswordInvalidException() {
    setCode("org.ptit.okrs.core_authentication.exception.PasswordInvalidException");
  }
}
