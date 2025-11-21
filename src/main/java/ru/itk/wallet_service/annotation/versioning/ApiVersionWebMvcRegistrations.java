package ru.itk.wallet_service.annotation.versioning;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * Регистрирует кастомный RequestMappingHandlerMapping,
 * который добавляет к путям версионный префикс (/v{n}).
 */
@RequiredArgsConstructor
public class ApiVersionWebMvcRegistrations implements WebMvcRegistrations {

  @Override
  public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
    return new VersionedRequestMappingHandlerMapping();
  }
}
