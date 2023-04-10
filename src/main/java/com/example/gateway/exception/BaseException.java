package com.example.gateway.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {

  private static final long serialVersionUID = 128763123L;

  private int status = 0;
  private String code = "";
  private String message = "";
  private Map<String, String> params;

  public void addParams(String key, String value) {
    if (Objects.isNull(params)) {
      params = new HashMap<>();
    }
    params.put(key, value);
  }
}
