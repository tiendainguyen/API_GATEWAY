package com.example.gateway.service;

import com.example.gateway.entity.AuthAccount;
import com.example.gateway.repository.AccountUserProjection;

public interface AuthAccountService {
  // dungf cho filter
  AuthAccount findByUserIdWithThrow(String userId);
  AccountUserProjection findByUsername(String username);
  void enableLockPermanent(String email);
  void activeAccount(String userId);
  AuthAccount create(String userId, String username, String password);
  void updatePasswordByEmail(String email, String password);
  void disableLockPermanent(String email);
}
