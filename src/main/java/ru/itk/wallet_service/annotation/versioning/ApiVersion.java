package ru.itk.wallet_service.annotation.versioning;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Обозначает версию API для эндпоинта.
 * Пример: {@code @ApiVersion("1")}
 * Путь эндпоинта будет дополнен префиксом /v1.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
  /**
   * Версия API, например "1"
   */
  String value();
}