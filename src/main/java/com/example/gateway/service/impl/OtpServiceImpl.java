package com.example.gateway.service.impl;

import com.example.gateway.exception.OTPInvalidException;
import com.example.gateway.service.OtpService;
import java.util.Objects;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

public class OtpServiceImpl extends BaseRedisServiceImpl<String> implements OtpService {


  public OtpServiceImpl(RedisTemplate<String, Object> redisTemplate, long timeOut, TimeUnit unitTimeOut) {
    super(redisTemplate, timeOut, unitTimeOut);
  }

  @Override
  protected boolean isSavePersistent() {
    return false;
  }

  @Override
  public void validateOtp(String email, String otpRequest) {
    var otpCache = get(email);
    if (!Objects.equals(otpCache, otpRequest)) {
      throw new OTPInvalidException();
    }
  }
}
