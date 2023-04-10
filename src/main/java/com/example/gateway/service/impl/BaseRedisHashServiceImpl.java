package com.example.gateway.service.impl;

import com.example.gateway.service.BaseRedisHashService;
import com.example.gateway.util.MapperUtil;
import java.util.Map;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

public class BaseRedisHashServiceImpl<T> implements BaseRedisHashService<T> {

  private final HashOperations<String, String, Object> hashOperations;

  public BaseRedisHashServiceImpl(RedisTemplate<String, Object> redisTemplate) {
    this.hashOperations = redisTemplate.opsForHash();
  }

  @Override
  public void delete(String key) {
    hashOperations.delete(key);
  }

  public void delete(String key, String field) {
    hashOperations.delete(key, field);
  }

  @Override
  public Map<String, Object> get(String key) {
    return hashOperations.entries(key);
  }

  @Override
  public Object get(String key, String field) {
    return hashOperations.get(key, field);
  }

  @Override
  public void set(String key, T object) {
    var data = MapperUtil.toMap(object);
    hashOperations.putAll(key, data);
  }

  @Override
  public void set(String key, String field, Object value) {
    hashOperations.put(key, field, value);
  }
}
