package com.example.gateway.exception;


public class OTPInvalidException extends BadRequestException {

  public OTPInvalidException() {
    setCode("org.ptit.okrs.core_authentication.exception.OTPInvalidException");
    setStatus(400);
  }
}