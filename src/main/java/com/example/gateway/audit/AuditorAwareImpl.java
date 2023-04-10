package com.example.gateway.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public Optional<String> getCurrentAuditor() {
    String userId = "SYSTEM";
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
    }

    return Optional.of(userId);
  }
}
