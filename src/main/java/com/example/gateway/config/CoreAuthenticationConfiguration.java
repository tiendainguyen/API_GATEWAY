package com.example.gateway.config;

import com.example.gateway.audit.AuditorAwareImpl;
import com.example.gateway.constant.EmailConstant;
import com.example.gateway.facade.AuthFacadeService;
import com.example.gateway.facade.AuthFacadeServiceImpl;
import com.example.gateway.repository.AuthAccountRepository;
import com.example.gateway.repository.AuthUserRepository;
import com.example.gateway.service.AuthAccountService;
import com.example.gateway.service.AuthTokenService;
import com.example.gateway.service.AuthUserService;
import com.example.gateway.service.EmailService;
import com.example.gateway.service.LoginFailService;
import com.example.gateway.service.MessageService;
import com.example.gateway.service.OtpService;
import com.example.gateway.service.ResetKeyService;
import com.example.gateway.service.TokenRedisService;
import com.example.gateway.service.impl.AuthAccountServiceImpl;
import com.example.gateway.service.impl.AuthTokenServiceImpl;
import com.example.gateway.service.impl.AuthUserServiceImpl;
import com.example.gateway.service.impl.LoginFailServiceImpl;
import com.example.gateway.service.impl.MessageServiceImpl;
import com.example.gateway.service.impl.OtpServiceImpl;
import com.example.gateway.service.impl.ResetKeyServiceImpl;
import com.example.gateway.service.impl.TokenRedisServiceImpl;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration
public class CoreAuthenticationConfiguration {

  @Value("${application.authentication.access_token.jwt_secret:xxx}")
  private String accessTokenJwtSecret;

  @Value("${application.authentication.access_token.life_time}")
  private Long accessTokenLifeTime;

  @Value("${application.authentication.refresh_token.jwt_secret:xxx}")
  private String refreshTokenJwtSecret;

  @Value("${application.authentication.refresh_token.life_time}")
  private Long refreshTokenLifeTime;

  @Value("${application.authentication.redis.otp_time_out:3}")
  private Integer redisOtpTimeOut;

  @Bean
  public AuditorAware<String> AuthAuditorAware() {
    return new AuditorAwareImpl();
  }

  @Bean
  public AuthAccountService authAccountService(AuthAccountRepository repository) {
    return new AuthAccountServiceImpl(repository);
  }

  @Bean
  public AuthFacadeService authFacadeService(
      AuthAccountService authAccountService,
      AuthUserService authUserService,
      AuthTokenService authTokenService,
      OtpService otpService,
      TokenRedisService tokenRedisService,
      EmailService emailService,
      ResetKeyService resetKeyService,
      PasswordEncoder passwordEncoder,
      MessageService messageService,
      LoginFailService loginFailService) {
    return new AuthFacadeServiceImpl(
        authAccountService,
        authUserService,
        authTokenService,
        otpService,
        tokenRedisService,
        accessTokenLifeTime,
        refreshTokenLifeTime,
        emailService,
        resetKeyService,
        passwordEncoder,
        messageService,
        loginFailService);
  }

  @Bean
  public AuthUserService authUserService(AuthUserRepository repository) {
    return new AuthUserServiceImpl(repository);
  }

  @Bean
  public AuthTokenService authTokenService() {
    return new AuthTokenServiceImpl(
        accessTokenJwtSecret, accessTokenLifeTime, refreshTokenJwtSecret, refreshTokenLifeTime);
  }

  @Bean
  public LoginFailService loginFailService(
      RedisTemplate<String, Object> redisTemplate, AuthAccountService authAccountService) {
    return new LoginFailServiceImpl(redisTemplate, authAccountService);
  }

  @Bean
  public MessageService messageService(MessageSource messageSource) {
    return new MessageServiceImpl(messageSource);
  }

  @Bean
  public OtpService otpService(RedisTemplate<String, Object> redisTemplate) {
    return new OtpServiceImpl(redisTemplate, redisOtpTimeOut, TimeUnit.MINUTES);
  }

  @Bean
  public TokenRedisService tokenRedisService(RedisTemplate<String, Object> redisTemplate) {
    return new TokenRedisServiceImpl(redisTemplate);
  }

  @Bean
  public ResetKeyService resetKeyService(RedisTemplate<String, Object> redisTemplate) {
    return new ResetKeyServiceImpl(redisTemplate);
  }
  @Bean
  public SpringTemplateEngine springTemplateEngine() {
    var templateEngine = new SpringTemplateEngine();
    templateEngine.addTemplateResolver(emailTemplateSolver());
    return templateEngine;
  }

  @Bean
  public ClassLoaderTemplateResolver emailTemplateSolver() {
    var emailTemplateSolver = new ClassLoaderTemplateResolver();
    emailTemplateSolver.setPrefix(EmailConstant.EMAIL_TEMPLATE_PREFIX);
    emailTemplateSolver.setSuffix(EmailConstant.EMAIL_TEMPLATE_SUFFIX);
    emailTemplateSolver.setTemplateMode(TemplateMode.HTML);
    emailTemplateSolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
    emailTemplateSolver.setCacheable(false);
    return emailTemplateSolver;
  }
}
