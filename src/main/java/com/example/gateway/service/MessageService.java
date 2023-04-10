package com.example.gateway.service;

import java.util.Locale;
import java.util.Map;

public interface MessageService {
  String getI18nMessage(String code, Locale locale, Map<String, String> params);
}
