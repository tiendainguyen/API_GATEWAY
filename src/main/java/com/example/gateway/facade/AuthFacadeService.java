package com.example.gateway.facade;

import com.example.gateway.dto.request.AuthUnlockAccountRequest;
import com.example.gateway.dto.request.AuthUserActiveAccountRequest;
import com.example.gateway.dto.request.AuthUserForgotPasswordOtpVerifyRequest;
import com.example.gateway.dto.request.AuthUserForgotPasswordResetRequest;
import com.example.gateway.dto.request.AuthUserLoginRequest;
import com.example.gateway.dto.request.AuthUserRegisterRequest;
import com.example.gateway.dto.request.AuthUserResetPasswordRequest;
import com.example.gateway.dto.request.AuthUserSentOtpToMail;
import com.example.gateway.dto.response.AuthUserForgotPasswordOtpVerifyResponse;
import com.example.gateway.dto.response.AuthUserLoginResponse;
import com.example.gateway.dto.response.AuthUserRegisterResponse;
import java.util.Locale;

public interface AuthFacadeService {

  /**
   * active account task in auth facade
   * @param request - email and otp user want to active
   */
  void activeAccount(AuthUserActiveAccountRequest request);

  /**
   * authenticate user
   * @param username - username of account
   * @param userId - id of user
   */
  void authenticate(String username, String userId);
  /**
   * validate login request and authenticate user
   * @param request - login request from client
   * @param locale - locale to return inactive account message
   * @return a login response
   */
  AuthUserLoginResponse login(AuthUserLoginRequest request, Locale locale);

  /**
   * register account user
   * @param request - registered user information
   * @return - recently registered user information
   */
  AuthUserRegisterResponse register(AuthUserRegisterRequest request);

  /**
   * forgot password function
   * @param request - email to send the otp code to the user
   */
  void forgotPassword(AuthUserResetPasswordRequest request);

  /**
   * otp code verification function forgot password
   * @param request - email and otp to be able to verify
   * @return - password new
   */
  AuthUserForgotPasswordOtpVerifyResponse verifyOtpForgotPassword(
      AuthUserForgotPasswordOtpVerifyRequest request);

  /**
   * password reset function
   * @param request - User information to change password
   */
  void resetPassword(AuthUserForgotPasswordResetRequest request);

  /**
   * send otp to mail
   * @param request - email to send the otp code to the user
   */
  void unlockAccount(AuthUserSentOtpToMail request);

  /**
   * unlock account
   * @param request - email want to unlock account
   */
  void verifyOtpUnlockAccount(AuthUnlockAccountRequest request);
}
