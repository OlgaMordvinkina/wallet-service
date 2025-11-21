package ru.itk.wallet_service.handler.exception;

import ru.itk.wallet_service.enums.EntityType;

public class EntityNotFoundException extends RuntimeException {

  private static final String MESSAGE_BY_ID_FORMAT = "%s с id %s не существует";

  public EntityNotFoundException(EntityType entityType, Object id) {
    super(String.format(MESSAGE_BY_ID_FORMAT, entityType.getName(), id));
  }

  public EntityNotFoundException(String message) {
    super(message);
  }

}
