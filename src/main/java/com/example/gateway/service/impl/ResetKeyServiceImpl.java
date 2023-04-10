package com.example.gateway.service.impl;

import com.example.gateway.service.ResetKeyService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

public class ResetKeyServiceImpl extends BaseRedisHashServiceImpl<String>
    implements ResetKeyService {

  public ResetKeyServiceImpl(RedisTemplate<String, Object> redisTemplate) {
    super(redisTemplate);
  }
}
