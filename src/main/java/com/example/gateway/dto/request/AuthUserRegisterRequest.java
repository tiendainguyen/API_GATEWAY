package com.example.gateway.dto.request;

import com.example.gateway.exception.PasswordConfirmNotMatchException;
import com.example.gateway.validation.ValidateEmail;
import com.example.gateway.validation.ValidateUsername;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthUserRegisterRequest {
  @NotBlank
  @ValidateEmail
  private String email;

  @NotBlank @ValidateUsername
  private String username;

  @NotBlank
  @Size(min = 8)
  private String password;

  @NotBlank
  @Size(min = 8)
  private String passwordConfirm;

  public void validate() {
    if (!Objects.equals(password, passwordConfirm)) {
      throw new PasswordConfirmNotMatchException();
    }
  }
}
