package com.example.gateway.dto.request;

import com.example.gateway.validation.ValidateEmail;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class AuthUserForgotPasswordOtpVerifyRequest {
  @NotBlank
  @ValidateEmail
  private String email;

  @NotBlank
  private String otp;
}
