package ru.itk.wallet_service.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.itk.wallet_service.annotation.versioning.ApiVersion;
import ru.itk.wallet_service.mapper.WalletMapper;
import ru.itk.wallet_service.service.WalletService;
import ru.itk.wallet_service.web.dto.SaveWalletDto;
import ru.itk.wallet_service.web.dto.WalletDto;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "Операции с кошельками", description = "API для работы с кошельками")
public class WalletController {
  private final WalletService service;
  private final WalletMapper mapper;

  @ApiVersion("1")
  @Operation(
    summary = "Создать новый кошелек",
    description = "Создает новый кошелек с уникальным UUID и начальным балансом 0. Возвращает созданный кошелек.",
    responses = {
      @ApiResponse(responseCode = "201", description = "Кошелек успешно создан",
        content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = WalletDto.class))),
      @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content)
    }
  )
  @PostMapping("/wallet/create")
  @ResponseStatus(HttpStatus.CREATED)
  public WalletDto createWallet() {
    log.debug("Request for POST create Wallet started");
    return mapper.toDto(service.createWallet());
  }

  @ApiVersion("1")
  @Operation(summary = "Изменить баланс кошелька",
    description = "Пополнение или снятие средств с указанного кошелька. Возвращает обновлённый баланс.",
    responses = {
      @ApiResponse(responseCode = "200", description = "Операция выполнена успешно",
        content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = WalletDto.class))),
      @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = @Content),
      @ApiResponse(responseCode = "404", description = "Кошелек не найден", content = @Content),
      @ApiResponse(responseCode = "409", description = "Недостаточно средств для снятия", content = @Content)
    })
  @PostMapping("/wallet")
  public WalletDto updateWallet(@Valid @RequestBody SaveWalletDto dto) {
    log.debug("Request for POST update Wallet started");
    return mapper.toDto(service.updateWallet(dto));
  }

  @ApiVersion("1")
  @Operation(summary = "Получить баланс кошелька",
    description = "Возвращает текущую сумму на кошельке по его UUID",
    responses = {
      @ApiResponse(responseCode = "200", description = "Баланс успешно получен",
        content = @Content(mediaType = "application/json",
          schema = @Schema(implementation = WalletDto.class))),
      @ApiResponse(responseCode = "404", description = "Кошелек не найден", content = @Content)
    })
  @GetMapping("/wallets/{walletId}")
  public WalletDto getBalance(@PathVariable @NotNull(message = "{default.valid.notNull}") UUID walletId) {
    log.debug("Request for GET Wallet by id started");
    return mapper.toDto(service.getBalance(walletId));
  }

}

