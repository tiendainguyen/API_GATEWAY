package com.example.gateway.facade;


import static com.example.gateway.constant.CacheConstant.CacheToken.KEY_CACHE_ACCESS_TOKEN;
import static com.example.gateway.constant.CacheConstant.CacheToken.KEY_CACHE_REFRESH_TOKEN;
import static com.example.gateway.constant.PropertiesConstant.INACTIVE_ACCOUNT_MESSAGE_CODE;

import com.example.gateway.constant.CacheConstant.CacheResetPassword;
import com.example.gateway.constant.CacheConstant.CacheVerifyOtpForgotPassword;
import com.example.gateway.constant.MailConstant;
import com.example.gateway.constant.MailConstant.MailForgotPassword;
import com.example.gateway.constant.MailConstant.MailRegister;
import com.example.gateway.constant.MailConstant.MailUnlockAccount;
import com.example.gateway.dto.request.AuthUnlockAccountRequest;
import com.example.gateway.dto.request.AuthUserActiveAccountRequest;
import com.example.gateway.dto.request.AuthUserForgotPasswordOtpVerifyRequest;
import com.example.gateway.dto.request.AuthUserForgotPasswordResetRequest;
import com.example.gateway.dto.request.AuthUserLoginRequest;
import com.example.gateway.dto.request.AuthUserRegisterRequest;
import com.example.gateway.dto.request.AuthUserResetPasswordRequest;
import com.example.gateway.dto.request.AuthUserSentOtpToMail;
import com.example.gateway.dto.response.AuthActiveUserResponse;
import com.example.gateway.dto.response.AuthInactiveUserResponse;
import com.example.gateway.dto.response.AuthUserForgotPasswordOtpVerifyResponse;
import com.example.gateway.dto.response.AuthUserLoginResponse;
import com.example.gateway.dto.response.AuthUserRegisterResponse;
import com.example.gateway.exception.PasswordConfirmNotMatchException;
import com.example.gateway.exception.PasswordInvalidException;
import com.example.gateway.exception.ResetKeyInvalidException;
import com.example.gateway.service.AuthAccountService;
import com.example.gateway.service.AuthTokenService;
import com.example.gateway.service.AuthUserService;
import com.example.gateway.service.EmailService;
import com.example.gateway.service.LoginFailService;
import com.example.gateway.service.MessageService;
import com.example.gateway.service.OtpService;
import com.example.gateway.service.ResetKeyService;
import com.example.gateway.service.TokenRedisService;
import com.example.gateway.util.CryptUtil;
import com.example.gateway.util.GeneratorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthFacadeServiceImpl implements AuthFacadeService {

  private final AuthAccountService authAccountService;
  private final AuthUserService authUserService;
  private final AuthTokenService authTokenService;
  private final OtpService otpService;
  private final TokenRedisService tokenRedisService;
  private final EmailService emailService;
  private final Long accessTokenLifeTime;
  private final Long refreshTokenLifeTime;
  private final ResetKeyService resetKeyService;
  private final PasswordEncoder passwordEncoder;
  private final MessageService messageService;

  private final LoginFailService loginFailService;

  @Value("${application.authentication.redis.otp_time_out}")
  private Integer otpTimeLife;

  public AuthFacadeServiceImpl(
      AuthAccountService authAccountService,
      AuthUserService authUserService,
      AuthTokenService authTokenService,
      OtpService otpService,
      TokenRedisService tokenRedisService,
      Long accessTokenLifeTime,
      Long refreshTokenLifeTime,
      EmailService emailService,
      ResetKeyService resetKeyService,
      PasswordEncoder passwordEncoder,
      MessageService messageService,
      LoginFailService loginFailService) {
    this.authAccountService = authAccountService;
    this.authUserService = authUserService;
    this.authTokenService = authTokenService;
    this.otpService = otpService;
    this.tokenRedisService = tokenRedisService;
    this.emailService = emailService;
    this.accessTokenLifeTime = accessTokenLifeTime;
    this.refreshTokenLifeTime = refreshTokenLifeTime;
    this.resetKeyService = resetKeyService;
    this.passwordEncoder = passwordEncoder;
    this.messageService = messageService;
    this.loginFailService = loginFailService;
  }

  @Override
  public void activeAccount(AuthUserActiveAccountRequest request) {
    authUserService.validateExistedWithEmail(request.getEmail());
    String idUser = authUserService.findIdByEmail(request.getEmail());
    otpService.validateOtp(request.getEmail(), request.getOtp());
    authAccountService.activeAccount(idUser);
  }

  @Override
  public void authenticate(String username, String userId) {
    var user = authUserService.findById(userId);
    var usernamePasswordAuthToken =
        new UsernamePasswordAuthenticationToken(username, userId, new ArrayList<>());
    usernamePasswordAuthToken.setDetails(user);
    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthToken);
  }

  @Override
  public AuthUserLoginResponse login(AuthUserLoginRequest request, Locale locale) {
    var accountUser = authAccountService.findByUsername(request.getUsername());
    if (!accountUser.getIsActivated()) {
      return AuthInactiveUserResponse.from(
          messageService.getI18nMessage(INACTIVE_ACCOUNT_MESSAGE_CODE, locale, null));
    }

    loginFailService.checkLock(
        accountUser.getEmail(), accountUser.getUserId(), accountUser.getIsLockPermanent());

    if (!CryptUtil.getPasswordEncoder().matches(request.getPassword(), accountUser.getPassword())) {
      loginFailService.increaseFailAttempts(accountUser.getEmail());
      loginFailService.setLock(accountUser.getEmail());
      throw new PasswordInvalidException();
    }
    loginFailService.resetFailAttempts(accountUser.getEmail());
    String accessToken =
        authTokenService.generateAccessToken(
            accountUser.getUserId(), accountUser.getEmail(), accountUser.getUsername());
    String refreshToken =
        authTokenService.generateRefreshToken(
            accountUser.getUserId(), accountUser.getEmail(), accountUser.getUsername());
    tokenRedisService.set(KEY_CACHE_ACCESS_TOKEN, accountUser.getUserId(), accessToken);
    tokenRedisService.set(KEY_CACHE_REFRESH_TOKEN, accountUser.getUserId(), refreshToken);
    authenticate(accountUser.getUsername(), accountUser.getUserId());
    return AuthActiveUserResponse.from(
        accessToken, refreshToken, accessTokenLifeTime, refreshTokenLifeTime);
  }

  @Override
  public AuthUserRegisterResponse register(AuthUserRegisterRequest request) {


    request.validate();

    // create account
    var authUser = authUserService.create(request.getEmail());
    var authAccount =
        authAccountService.create(
            authUser.getId(),
            request.getUsername(),
            CryptUtil.getPasswordEncoder().encode(request.getPassword()));

    // generate otp and push it to redis
    var otpActiveAccount = GeneratorUtils.generateOtp();
    otpService.set(authUser.getEmail(), otpActiveAccount);
    // Send mail request active account
    sendMailOTPTemplate(
        request.getEmail(),
        otpActiveAccount,
        MailRegister.KEY_PARAM_OTP_TIME_LIFE,
        MailRegister.KEY_PARAM_OTP,
        MailRegister.SUBJECT);

    return AuthUserRegisterResponse.of(
        authUser.getId(),
        authUser.getEmail(),
        authAccount.getId(),
        authAccount.getUsername(),
        authAccount.getIsActivated());
  }

  @Override
  public void forgotPassword(AuthUserResetPasswordRequest request) {

    // Step 1: validate exist identifier
    authUserService.validateExistedWithEmail(request.getEmail());

    // Step 2: if identifier found -> generate otp, push redis and send email verify
    // generate otp and push it to redis
    var otpForgotPassword = GeneratorUtils.generateOtp();
    otpService.set(request.getEmail(), otpForgotPassword);
    // send mail verify
    sendMailOTPTemplate(
        request.getEmail(),
        otpForgotPassword,
        MailForgotPassword.KEY_PARAM_OTP_TIME_LIFE,
        MailForgotPassword.KEY_PARAM_OTP,
        MailForgotPassword.SUBJECT);
  }

  @Override
  public AuthUserForgotPasswordOtpVerifyResponse verifyOtpForgotPassword(
      AuthUserForgotPasswordOtpVerifyRequest request) {


    // chek email exist
    authUserService.validateExistedWithEmail(request.getEmail());

    // verify otp
    otpService.validateOtp(request.getEmail(), request.getOtp());

    // generate reset password key, push redis(key: email, value: resetKey), return to client
    var resetPasswordKey = CryptUtil.generateResetKey(request.getEmail());
    resetKeyService.set(
        CacheVerifyOtpForgotPassword.KEY_CACHE_RESET_PASSWORD,
        request.getEmail(),
        resetPasswordKey);

    return AuthUserForgotPasswordOtpVerifyResponse.of(resetPasswordKey);
  }

  @Override
  public void resetPassword(AuthUserForgotPasswordResetRequest request) {


    validatePassword(request.getNewPassword(), request.getNewPasswordConfirm());

    // validate email not found
    authUserService.validateExistedWithEmail(request.getEmail());

    // validate resetKey
    validateResetKey(request);

    // update new password
    authAccountService.updatePasswordByEmail(
        request.getEmail(), passwordEncoder.encode(request.getNewPassword()));
  }

  private void sendMailOTPTemplate(
      String email, String otp, String keyParamTimeLife, String keyParamOtp, String subject) {

    var params = new HashMap<String, Object>();
    params.put(keyParamTimeLife, otpTimeLife);
    params.put(keyParamOtp, otp);
    emailService.send(subject, email, MailConstant.OTP_TEMPLATE_NAME, params);
  }

  @Override
  public void unlockAccount(AuthUserSentOtpToMail request) {

    authUserService.validateExistedWithEmail(request.getEmail());

    var otpUnlockAccount = GeneratorUtils.generateOtp();
    otpService.set(request.getEmail(), otpUnlockAccount);
    sendMailOTPTemplate(
        request.getEmail(),
        otpUnlockAccount,
        MailUnlockAccount.KEY_PARAM_OTP_TIME_LIFE,
        MailUnlockAccount.KEY_PARAM_OTP,
        MailUnlockAccount.SUBJECT);
  }

  @Override
  public void verifyOtpUnlockAccount(AuthUnlockAccountRequest request) {


    authUserService.validateExistedWithEmail(request.getEmail());

    otpService.validateOtp(request.getEmail(), request.getOtp());

    authAccountService.disableLockPermanent(request.getEmail());
    loginFailService.resetFailAttempts(request.getEmail());
  }

  private void validateResetKey(AuthUserForgotPasswordResetRequest request) {
    if (!Objects.equals(
        resetKeyService.get(CacheResetPassword.KEY_CACHE_RESET_PASSWORD, request.getEmail()),
        request.getResetPasswordKey())) {

      throw new ResetKeyInvalidException();
    }
  }

  private void validatePassword(String newPassword, String newPasswordConfirm) {
    if (!Objects.equals(newPassword, newPasswordConfirm)) {
      throw new PasswordConfirmNotMatchException();
    }
  }
}
