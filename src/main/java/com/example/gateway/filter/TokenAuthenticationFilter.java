package com.example.gateway.filter;

import com.example.gateway.service.AuthAccountService;
import com.example.gateway.service.AuthTokenService;
import com.example.gateway.service.AuthUserService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import javax.servlet.ServletException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@AllArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
  private final AuthTokenService authTokenService;
  private final AuthUserService authUserService;
  private final AuthAccountService authAccountService;
  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, javax.servlet.FilterChain filterChain)
      throws ServletException, IOException {
    final String accessToken = request.getHeader("Authorization");

    if (Objects.isNull(accessToken)) {
      filterChain.doFilter(request, response);
      return;
    }

    if (!accessToken.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    var jwtToken = accessToken.substring(7);
    String userId;
    try {
      userId = authTokenService.getSubjectFromAccessToken(jwtToken);
    } catch (Exception ex) {
      filterChain.doFilter(request, response);
      return;
    }
    if (Objects.nonNull(userId)
        && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {
      var user = authUserService.findById(userId);
      var account = authAccountService.findByUserIdWithThrow(user.getId());
      if (authTokenService.validateAccessToken(jwtToken, userId)) {
        var usernamePasswordAuthToken =
            new UsernamePasswordAuthenticationToken(
                account.getUsername(), user.getId(), new ArrayList<>());
        usernamePasswordAuthToken.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthToken);
      }
    }

    filterChain.doFilter(request, response);
  }
}
