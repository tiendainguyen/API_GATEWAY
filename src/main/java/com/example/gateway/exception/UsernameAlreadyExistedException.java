package com.example.gateway.exception;

public class UsernameAlreadyExistedException extends ConflictException {
  public UsernameAlreadyExistedException(String username) {
    addParams("username", username);
    setCode("org.ptit.okrs.core_authentication.exception.UsernameAlreadyExistedException");
  }
}
