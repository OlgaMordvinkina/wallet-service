package ru.itk.wallet_service.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class AppConfig {

  /**
   * Загружает сообщения из resources/messages/messages.properties
   * и устанавливает кодировку UTF-8.
   *
   * @return настроенный {@link MessageSource} для работы с локализованными сообщениями
   */
  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:messages/messages");
    messageSource.setDefaultEncoding("UTF-8");
    return messageSource;
  }

}
