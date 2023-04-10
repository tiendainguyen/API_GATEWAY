package com.example.gateway.dto.request;

import com.example.gateway.validation.ValidateUsername;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthUserLoginRequest {

  @NotBlank
  @ValidateUsername private String username;

  @NotBlank private String password;
}
