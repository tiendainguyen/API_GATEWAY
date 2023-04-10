package com.example.gateway.dto.request;

import com.example.gateway.validation.ValidateEmail;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthUserForgotPasswordResetRequest {
  @NotBlank
  private String resetPasswordKey;
  @NotBlank
  @ValidateEmail
  private String email;
  @NotBlank
  private String newPassword;
  @NotBlank
  private String newPasswordConfirm;
}
