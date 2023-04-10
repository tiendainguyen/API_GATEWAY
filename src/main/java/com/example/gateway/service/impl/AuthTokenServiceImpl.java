package com.example.gateway.service.impl;

import com.example.gateway.service.AuthTokenService;
import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@AllArgsConstructor
public class AuthTokenServiceImpl implements AuthTokenService {
  @Value("${application.authentication.access_token.jwt_secret:xxx}")
  private String accessTokenJwtSecret;
  @Value("${application.authentication.access_token.life_time}")
  private Long accessTokenLifeTime;
  @Value("${application.authentication.refresh_token.jwt_secret:xxx}")
  private String refreshTokenJwtSecret;
  @Value("${application.authentication.refresh_token.life_time}")
  private Long refreshTokenLifeTime;
  @Override
  public String generateAccessToken(String userId, String email, String username) {
    var claims = new HashMap<String, Object>();
    claims.put("email", email);
    claims.put("username", username);
    return generateToken(userId, claims, accessTokenLifeTime, accessTokenJwtSecret);
  }
  // dùng cho filter
  @Override
  public String getSubjectFromAccessToken(String accessToken) {
    return null;
  }
// dùng cho Filter
  @Override
  public boolean validateAccessToken(String accessToken, String userId) {
    return getSubjectFromAccessToken(accessToken).equals(userId)
        && !isExpiredToken(accessToken, accessTokenJwtSecret);
  }

  @Override
  public String generateRefreshToken(String userId, String email, String username) {
    return null;
  }

  @Override
  public String getSubjectFromRefreshToken(String refreshToken) {
    return null;
  }

  @Override
  public boolean validateRefreshToken(String refreshToken, String userId) {
    return false;
  }
  private String generateToken(
      String subject, Map<String, Object> claims, long tokenLifeTime, String jwtSecret) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + tokenLifeTime))
        .signWith(SignatureAlgorithm.HS256, jwtSecret)
        .compact();
  }
  private boolean isExpiredToken(String token, String secretKey) {
    return getClaim(token, Claims::getExpiration, secretKey).before(new Date());
  }
  private <T> T getClaim(String token, Function<Claims, T> claimsResolve, String secretKey) {
    return claimsResolve.apply(getClaims(token, secretKey));
  }
  private Claims getClaims(String token, String secretKey) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
  }
}
