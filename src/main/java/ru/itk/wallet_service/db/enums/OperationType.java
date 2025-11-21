package ru.itk.wallet_service.db.enums;

/**
 * Тип операции со счётом.
 */
public enum OperationType {
  /**
   * Пополнение счёта.
   */
  DEPOSIT,

  /**
   * Снятие средств со счёта.
   */
  WITHDRAW
}
