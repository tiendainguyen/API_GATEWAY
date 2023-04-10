package com.example.gateway.exception;

public class LockedAccountException extends ForbiddenException {

  public LockedAccountException(String userId) {
    super(userId);
  }
}
