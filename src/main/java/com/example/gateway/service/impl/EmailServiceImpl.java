package com.example.gateway.service.impl;

import com.example.gateway.constant.EmailConstant;
import com.example.gateway.exception.InternalServerError;
import com.example.gateway.service.EmailService;
import java.util.Objects;
import javax.mail.Message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender emailSender;
  private final SpringTemplateEngine templateEngine;
  @Value("${application.email.from}")
  private String emailFrom;

  public EmailServiceImpl(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
    this.emailSender = emailSender;
    this.templateEngine = templateEngine;
  }

  @Async
  @Override
  public void send(String subject, String to, String content) {
    try {
      var message = new SimpleMailMessage();
      message.setFrom(emailFrom);
      message.setTo(to);
      message.setSubject(subject);
      message.setText(content);
      emailSender.send(message);
    } catch (Exception ex) {
      throw new InternalServerError("Send mail failed to email: {}" + to);
    }
  }

  @Async
  @Override
  public void send(String subject, String to, String template, Map<String, Object> properties) {
    try {
      var message = emailSender.createMimeMessage();
      message.setRecipients(Message.RecipientType.TO, to);
      message.setSubject(subject);
      message.setContent(getContent(template, properties), EmailConstant.CONTENT_TYPE_TEXT_HTML);
      emailSender.send(message);
    } catch (Exception ex) {
      throw new InternalServerError("Send mail failed to email: {}" + to);
    }
  }

  @Override
  public void send(String subject, String to, String content, String fileToAttach) {
    try{
      var message = emailSender.createMimeMessage();
      var helper = new MimeMessageHelper(message, true);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(content);

      FileSystemResource fileSystemResource = new FileSystemResource(fileToAttach);
      helper.addAttachment(Objects.requireNonNull(fileSystemResource.getFilename()), fileSystemResource);
      emailSender.send(message);
    } catch (Exception ex) {
      throw new InternalServerError("Send mail failed to email: {}" + to);
    }
  }

  private String getContent(String template, Map<String, Object> properties) {
    var context = new Context();
    context.setVariables(properties);
    return templateEngine.process(template, context);
  }
}
