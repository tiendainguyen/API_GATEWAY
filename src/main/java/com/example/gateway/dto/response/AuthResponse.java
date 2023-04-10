package com.example.gateway.dto.response;

import com.example.gateway.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class AuthResponse {
  private int status;
  private String timestamp;
  private Object data;

  public static AuthResponse of(int status, Object data) {
    return AuthResponse.of(status, DateUtils.getCurrentDateTimeStr(), data);
  }

  public static AuthResponse of(int status) {
    return AuthResponse.of(status, DateUtils.getCurrentDateTimeStr(), null);
  }
}
