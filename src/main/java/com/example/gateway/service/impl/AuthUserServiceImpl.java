package com.example.gateway.service.impl;

import com.example.gateway.entity.AuthUser;
import com.example.gateway.repository.AuthUserRepository;
import com.example.gateway.service.AuthUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {
  private final AuthUserRepository repository;
  @Override
  public AuthUser findById(String id) {
    return null;
  }

  @Override
  public AuthUser create(String email) {
    return null;
  }

  @Override
  public AuthUser findByEmail(String email) {
    return null;
  }

  @Override
  public void validateExistedWithEmail(String email) {

  }

  @Override
  public String findIdByEmail(String email) {
    return null;
  }
}
