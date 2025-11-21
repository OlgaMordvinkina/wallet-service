package ru.itk.wallet_service.annotation.versioning;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Обработчик маппингов, который добавляет версионный префикс
 * /v{X} ко всем эндпоинтам, помеченным аннотацией @ApiVersion.
 * <p>
 * Работает как для класса, так и для метода.
 */
@Slf4j
public class VersionedRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
  private static final String VERSION_PREFIX = "/v";
  private static final Pattern VERSION_PATTERN = Pattern.compile("\\d+");

  @Override
  protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
    // Получение базового маппинга, созданного Spring
    RequestMappingInfo info = super.getMappingForMethod(method, handlerType);
    if (info == null) return null;

    // Извлечение аннотации версии: приоритет у метода, затем у класса
    ApiVersion version = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion.class);
    if (version == null) {
      version = AnnotatedElementUtils.findMergedAnnotation(handlerType, ApiVersion.class);
    }

    // Если аннотация отсутствует — возвращается исходный маппинг
    if (version == null) {
      return info; // версия не указана — путь остаётся как есть
    }

    String v = version.value().trim();
    // Проверка корректности формата версии — допускаются только числовые значения
    if (!StringUtils.hasText(v) || !VERSION_PATTERN.matcher(v).matches()) {
      throw new IllegalArgumentException("@ApiVersion должен содержать только число. Найдено: " + v);
    }

    // Формирование URL-префикса версии
    String prefix = VERSION_PREFIX + v;

    // Добавление префикса к существующему маппингу маршрута
    return RequestMappingInfo
      .paths(prefix)
      .build()
      .combine(info);
  }
}
