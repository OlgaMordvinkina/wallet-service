package ru.itk.wallet_service.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletDto {

  @Schema(description = "Идентификатор кошелька")
  UUID walletId;

  @Schema(description = "Сумма операции")
  BigDecimal amount;
}
