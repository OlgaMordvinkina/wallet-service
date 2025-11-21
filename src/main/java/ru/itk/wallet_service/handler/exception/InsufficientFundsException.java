package ru.itk.wallet_service.handler.exception;

import lombok.Getter;

import java.util.UUID;

/**
 * Исключение выбрасывается, когда при попытке списания средств
 * на счёте недостаточно средств для проведения операции.
 */
@Getter
public class InsufficientFundsException extends RuntimeException {

  private static final String MESSAGE_FORMAT = "Недостаточно средств на счёте %s для списания суммы %s";

  private final UUID walletId;
  private final Number attemptedAmount;

  public InsufficientFundsException(UUID walletId, Number attemptedAmount) {
    super(String.format(MESSAGE_FORMAT, walletId, attemptedAmount));
    this.walletId = walletId;
    this.attemptedAmount = attemptedAmount;
  }
}