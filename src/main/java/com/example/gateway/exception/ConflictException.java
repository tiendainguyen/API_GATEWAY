package com.example.gateway.exception;

public class ConflictException extends BaseException {
  public ConflictException() {
    setStatus(409);
    setCode("org.ptit.okrs.core_exception.ConflictException");
  }
}
