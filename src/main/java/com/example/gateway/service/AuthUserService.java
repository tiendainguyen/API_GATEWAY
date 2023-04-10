package com.example.gateway.service;

import com.example.gateway.entity.AuthUser;

public interface AuthUserService {
  AuthUser findById(String id);

  AuthUser create(String email);

  AuthUser findByEmail(String email);

  void validateExistedWithEmail(String email);

  String findIdByEmail(String email);
}
