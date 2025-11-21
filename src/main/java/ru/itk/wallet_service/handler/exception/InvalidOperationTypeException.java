package ru.itk.wallet_service.handler.exception;

import lombok.Getter;
import ru.itk.wallet_service.db.enums.OperationType;

/**
 * Исключение выбрасывается, когда передан неизвестный или неподдерживаемый тип операции.
 */
@Getter
public class InvalidOperationTypeException extends RuntimeException {

  private static final String MESSAGE_FORMAT = "Неизвестный тип операции: %s";

  private final OperationType operationType;

  public InvalidOperationTypeException(OperationType operationType) {
    super(String.format(MESSAGE_FORMAT, operationType));
    this.operationType = operationType;
  }

}
