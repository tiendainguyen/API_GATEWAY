package com.example.gateway.dto.request;

import com.example.gateway.validation.ValidateEmail;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthUserSentOtpToMail {

  @NotBlank
  @ValidateEmail
  private String email;
}
