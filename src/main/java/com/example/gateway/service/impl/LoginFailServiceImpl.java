package com.example.gateway.service.impl;

import static com.example.gateway.constant.CacheConstant.LoginFail.KEY_CACHE_FAIL_ATTEMPTS;
import static com.example.gateway.constant.CacheConstant.LoginFail.KEY_CACHE_UNLOCK_TIME;
import static com.example.gateway.constant.LoginFailConstant.FIRST_LOCK_LIMIT;
import static com.example.gateway.constant.LoginFailConstant.FIRST_LOCK_TIME;
import static com.example.gateway.constant.LoginFailConstant.INIT_FAIL_ATTEMPTS;
import static com.example.gateway.constant.LoginFailConstant.SECOND_LOCK_LIMIT;
import static com.example.gateway.constant.LoginFailConstant.SECOND_LOCK_TIME;
import static com.example.gateway.constant.LoginFailConstant.THIRD_LOCK_LIMIT;

import com.example.gateway.exception.PermanentLockException;
import com.example.gateway.exception.TemporaryLockException;
import com.example.gateway.service.AuthAccountService;
import com.example.gateway.service.LoginFailService;
import com.example.gateway.util.DateUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

public class LoginFailServiceImpl extends BaseRedisHashServiceImpl<Long>
    implements LoginFailService {

  private final AuthAccountService authAccountService;

  public LoginFailServiceImpl(
      RedisTemplate<String, Object> redisTemplate, AuthAccountService authAccountService) {
    super(redisTemplate);
    this.authAccountService = authAccountService;
  }

  @Override
  public Long getFailAttempts(String email) {
    return (Long) get(KEY_CACHE_FAIL_ATTEMPTS, email);
  }

  @Override
  public Long getUnlockTime(String email) {
    return (Long) get(KEY_CACHE_UNLOCK_TIME, email);
  }

  @Override
  public void increaseFailAttempts(String email) {
    Long failAttempts = (Long) get(KEY_CACHE_FAIL_ATTEMPTS, email);
    if (failAttempts == null) {
      failAttempts = INIT_FAIL_ATTEMPTS;
    }
    failAttempts++;
    set(KEY_CACHE_FAIL_ATTEMPTS, email, failAttempts);
  }

  @Override
  public Boolean isTemporaryLock(String email) {
    Long unlockTime = (Long) get(KEY_CACHE_UNLOCK_TIME, email);
    if (unlockTime == null) {
      return false;
    }
    return DateUtils.getCurrentEpoch() < unlockTime;
  }

  @Override
  public void resetFailAttempts(String email) {
    delete(KEY_CACHE_FAIL_ATTEMPTS, email);
    delete(KEY_CACHE_UNLOCK_TIME, email);
  }

  @Override
  @Transactional
  public void setLock(String email) {
    Long failAttempts = (Long) get(KEY_CACHE_FAIL_ATTEMPTS, email);
    if (failAttempts.equals(THIRD_LOCK_LIMIT)) {
      authAccountService.enableLockPermanent(email);
    }
    if (failAttempts.equals(SECOND_LOCK_LIMIT)) {
      set(KEY_CACHE_UNLOCK_TIME, email, DateUtils.getCurrentEpoch() + SECOND_LOCK_TIME);
    }
    if (failAttempts.equals(FIRST_LOCK_LIMIT)) {
      set(KEY_CACHE_UNLOCK_TIME, email, DateUtils.getCurrentEpoch() + FIRST_LOCK_TIME);
    }
  }

  @Override
  public void checkLock(String email, String userId, Boolean isLockPermanent) {

    if (isLockPermanent) {
      throw new PermanentLockException(userId, getFailAttempts(email));
    }
    if (isTemporaryLock(email)) {
      throw new TemporaryLockException(userId, getFailAttempts(email), getUnlockTime(email));
    }
  }
}
