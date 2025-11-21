package ru.itk.wallet_service.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.itk.wallet_service.db.enums.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveWalletDto {

  @Schema(description = "Идентификатор кошелька")
  @NotNull(message = "{default.valid.notNull}")
  UUID walletId;

  @Schema(description = "Тип операции")
  @NotNull(message = "{default.valid.notNull}")
  OperationType operationType;

  @Schema(description = "Сумма операции")
  @NotNull(message = "{default.valid.notNull}")
  @DecimalMin(value = "0.01", message = "{wallet.valid.amountMin}")
  BigDecimal amount;
}
