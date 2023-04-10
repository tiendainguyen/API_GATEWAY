package com.example.gateway.service.impl;

import com.example.gateway.service.TokenRedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

public class TokenRedisServiceImpl extends BaseRedisHashServiceImpl<String> implements
    TokenRedisService {

  public TokenRedisServiceImpl(
      RedisTemplate<String, Object> redisTemplate) {
    super(redisTemplate);
  }
}
