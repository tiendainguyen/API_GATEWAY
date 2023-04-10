package com.example.gateway.service;


public interface OtpService extends BaseRedisService<String> {

  /**
   * compare otp with otp save in cache
   * @param email - the email is key to save otp in cache
   * @param otpRequest - the otpReuqest is otp can compare
   */
  void validateOtp(String email, String otpRequest);

}