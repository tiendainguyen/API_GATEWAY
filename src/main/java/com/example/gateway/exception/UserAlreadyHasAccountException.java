package com.example.gateway.exception;

public class UserAlreadyHasAccountException extends ConflictException {
  public UserAlreadyHasAccountException() {
    setCode("org.ptit.okrs.core_authentication.exception.UserAlreadyHasAccountException");
  }
}
