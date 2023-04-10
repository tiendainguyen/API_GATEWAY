package com.example.gateway.controller;

import com.example.gateway.dto.request.AuthUnlockAccountRequest;
import com.example.gateway.dto.request.AuthUserActiveAccountRequest;
import com.example.gateway.dto.request.AuthUserForgotPasswordOtpVerifyRequest;
import com.example.gateway.dto.request.AuthUserForgotPasswordResetRequest;
import com.example.gateway.dto.request.AuthUserLoginRequest;
import com.example.gateway.dto.request.AuthUserRegisterRequest;
import com.example.gateway.dto.request.AuthUserResetPasswordRequest;
import com.example.gateway.dto.request.AuthUserSentOtpToMail;
import com.example.gateway.dto.response.AuthResponse;
import com.example.gateway.facade.AuthFacadeService;
import java.util.Locale;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth/users")
@RestController
public class AuthUserController {
  private final AuthFacadeService authFacadeService;

  public AuthUserController(AuthFacadeService authFacadeService) {
    this.authFacadeService = authFacadeService;
  }


  @PostMapping("/active")
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse activeAccount(@Valid @RequestBody AuthUserActiveAccountRequest request) {
    authFacadeService.activeAccount(request);
    return AuthResponse.of(HttpStatus.OK.value());
  }


  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public AuthResponse register(@Valid @RequestBody AuthUserRegisterRequest request) {
    return AuthResponse.of(HttpStatus.CREATED.value(), authFacadeService.register(request));
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse login(
      @Valid @RequestBody AuthUserLoginRequest request,
      @RequestHeader(value = "Accept-Language", required = false) Locale locale) {
    return AuthResponse.of(HttpStatus.OK.value(), authFacadeService.login(request, locale));
  }

  @PostMapping("/forgot-password")
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse forgotPassword(@Valid @RequestBody AuthUserResetPasswordRequest request) {
    authFacadeService.forgotPassword(request);
    return AuthResponse.of(HttpStatus.OK.value());
  }


  @PostMapping("/forgot-password/otp-verify")
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse verifyOtpForgotPassword(
      @Valid @RequestBody AuthUserForgotPasswordOtpVerifyRequest request) {
    return AuthResponse.of(HttpStatus.OK.value(), authFacadeService.verifyOtpForgotPassword(request));
  }


  @PostMapping("/forgot-password/reset")
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse resetPassword(@Valid @RequestBody AuthUserForgotPasswordResetRequest request) {
    authFacadeService.resetPassword(request);
    return AuthResponse.of(HttpStatus.OK.value());
  }


  @PostMapping("/unlock-account")
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse unlockAccount(@Valid @RequestBody AuthUserSentOtpToMail request) {
    authFacadeService.unlockAccount(request);
    return AuthResponse.of(HttpStatus.OK.value());
  }


  @PostMapping("/unlock-account/otp-verify")
  @ResponseStatus(HttpStatus.OK)
  public AuthResponse verifyOtpUnlockAccount(@Valid @RequestBody AuthUnlockAccountRequest request) {
    authFacadeService.verifyOtpUnlockAccount(request);
    return AuthResponse.of(HttpStatus.OK.value());
  }
}
