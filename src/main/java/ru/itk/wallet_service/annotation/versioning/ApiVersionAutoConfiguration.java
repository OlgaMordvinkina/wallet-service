package ru.itk.wallet_service.annotation.versioning;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Автоконфигурация, отвечающая за подключение механизма
 * версионирования REST-эндпоинтов через кастомный
 * {@link ApiVersionWebMvcRegistrations}.
 * <p>
 */
@AutoConfiguration
public class ApiVersionAutoConfiguration {

  /**
   * Регистрирует компонент, который подменяет стандартный
   * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}
   * на кастомный вариант, добавляющий поддержку аннотации @ApiVersion.
   */
  @Bean
  public ApiVersionWebMvcRegistrations apiVersionWebMvcRegistrations() {
    return new ApiVersionWebMvcRegistrations();
  }
}
