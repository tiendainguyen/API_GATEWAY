package com.example.gateway.dto.request;

import com.example.gateway.validation.ValidateEmail;
import javax.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class AuthUserResetPasswordRequest {

  //Email
  @NotBlank
  @ValidateEmail
  private String email;
}
