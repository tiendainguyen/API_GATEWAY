package com.example.gateway.service;

public interface AuthTokenService {
  /* ACCESS TOKEN */
  String generateAccessToken(String userId, String email, String username);
  String getSubjectFromAccessToken(String accessToken);
  boolean validateAccessToken(String accessToken, String userId);

  /* REFRESH TOKEN */
  String generateRefreshToken(String userId, String email, String username);
  String getSubjectFromRefreshToken(String refreshToken);
  boolean validateRefreshToken(String refreshToken, String userId);
}
