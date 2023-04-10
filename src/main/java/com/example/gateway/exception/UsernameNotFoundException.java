package com.example.gateway.exception;
public class UsernameNotFoundException extends BadRequestException {

  public UsernameNotFoundException(String username) {
    addParams("username", username);
    setCode("org.ptit.okrs.core_authentication.exception.UsernameNotFoundException");
  }
}