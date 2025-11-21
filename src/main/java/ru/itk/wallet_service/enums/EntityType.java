package ru.itk.wallet_service.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.itk.wallet_service.db.entity.Wallet;

@Getter
@AllArgsConstructor
public enum EntityType {
  WALLET(Wallet.class, "Кошелёк");

  private final Class<?> clazz;
  private final String name;
}
