package com.example.gateway.service.impl;

import com.example.gateway.entity.AuthAccount;
import com.example.gateway.entity.AuthUser;
import com.example.gateway.exception.NotFoundException;
import com.example.gateway.exception.UserAlreadyHasAccountException;
import com.example.gateway.exception.UsernameAlreadyExistedException;
import com.example.gateway.repository.AccountUserProjection;
import com.example.gateway.repository.AuthAccountRepository;
import com.example.gateway.service.AuthAccountService;
import lombok.AllArgsConstructor;
import com.example.gateway.exception.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class AuthAccountServiceImpl implements AuthAccountService {
  private final AuthAccountRepository repository;
  @Override
  public AuthAccount findByUserIdWithThrow(String userId) {
    return repository
        .findFirstByUserId(userId)
        .orElseThrow(
            () -> {
              // . class la doi tuong gi nhi?
              throw new NotFoundException(userId,AuthUser.class.getSimpleName());
            });
  }
  @Override
  public AccountUserProjection findByUsername(String username) {
    return repository.find(username).orElseGet(() -> {
      throw new UsernameNotFoundException(username);
    });
  }
  @Override
  public void enableLockPermanent(String email) {
    repository.enableLockPermanent(email);
  }
  @Override
  public void activeAccount(String userId) {
    repository.activeAccount(userId);
  }
  @Override
  @Transactional
  public AuthAccount create(String userId, String username, String password) {

    if (repository.existsByUserId(userId)) {
      throw new UserAlreadyHasAccountException();
    }

    if (repository.existsByUsername(username)) {
      throw new UsernameAlreadyExistedException(username);
    }

    return repository.save(AuthAccount.of(userId, username, password));
  }
  @Override
  public void updatePasswordByEmail(String userId, String password) {
    repository.updatePasswordByEmail(userId, password);
  }
  @Override
  public void disableLockPermanent(String email) {
    repository.disableLockPermanent(email);
  }
}
