package com.example.gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AuthUserForgotPasswordOtpVerifyResponse {
  private String resetPasswordKey;
}
