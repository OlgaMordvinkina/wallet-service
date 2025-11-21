package ru.itk.wallet_service.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.itk.wallet_service.AbstractApplicationTest;
import ru.itk.wallet_service.PathUtils;
import ru.itk.wallet_service.db.entity.Wallet;
import ru.itk.wallet_service.db.enums.OperationType;
import ru.itk.wallet_service.enums.EntityType;
import ru.itk.wallet_service.handler.exception.EntityNotFoundException;
import ru.itk.wallet_service.handler.exception.InsufficientFundsException;
import ru.itk.wallet_service.handler.exception.InvalidOperationTypeException;
import ru.itk.wallet_service.mapper.WalletMapper;
import ru.itk.wallet_service.service.WalletService;
import ru.itk.wallet_service.web.dto.SaveWalletDto;
import ru.itk.wallet_service.web.dto.WalletDto;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WalletControllerTest extends AbstractApplicationTest {

  @MockitoBean
  private WalletService walletService;
  @MockitoBean
  private WalletMapper walletMapper;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void createWallet_shouldReturnCreatedWallet() throws Exception {
    Wallet wallet = buildWallet(UUID.randomUUID(), BigDecimal.valueOf(0.0));
    WalletDto walletDto = buildWalletDto(wallet.getWalletId(), wallet.getAmount());

    Mockito.when(walletService.createWallet()).thenReturn(wallet);
    Mockito.when(walletMapper.toDto(wallet)).thenReturn(walletDto);

    mockMvc.perform(post(getPath(PathUtils.CREATE)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.walletId").value(wallet.getWalletId().toString()))
      .andExpect(jsonPath("$.amount").value(wallet.getAmount()));
  }

  @Test
  void updateWallet_shouldReturnUpdatedWallet() throws Exception {
    SaveWalletDto saveDto = buildSaveWalletDto(UUID.randomUUID(), BigDecimal.valueOf(100.0), OperationType.DEPOSIT);

    Wallet updatedWallet = buildWallet(saveDto.getWalletId(), saveDto.getAmount());
    WalletDto walletDto = buildWalletDto(updatedWallet.getWalletId(), updatedWallet.getAmount());

    Mockito.when(walletService.updateWallet(any(SaveWalletDto.class))).thenReturn(updatedWallet);
    Mockito.when(walletMapper.toDto(updatedWallet)).thenReturn(walletDto);

    mockMvc.perform(post(getPath(PathUtils.UPDATE))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(saveDto)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.walletId").value(updatedWallet.getWalletId().toString()))
      .andExpect(jsonPath("$.amount").value(updatedWallet.getAmount()));
  }

  @Test
  void updateWallet_shouldReturnNotFound_ifWalletDoesNotExist() throws Exception {
    SaveWalletDto saveDto = buildSaveWalletDto(UUID.randomUUID(), BigDecimal.valueOf(50.0), OperationType.DEPOSIT);
    Mockito.when(walletService.updateWallet(any(SaveWalletDto.class)))
      .thenThrow(new EntityNotFoundException(EntityType.WALLET, saveDto.getWalletId()));

    performUpdateAndExpect(saveDto, 404, "Кошелёк с id %s не существует".formatted(saveDto.getWalletId()));
  }

  @Test
  void updateWallet_shouldReturnConflict_ifInsufficientFunds() throws Exception {
    SaveWalletDto saveDto = buildSaveWalletDto(UUID.randomUUID(), BigDecimal.valueOf(1000.0), OperationType.WITHDRAW);
    Mockito.when(walletService.updateWallet(any(SaveWalletDto.class)))
      .thenThrow(new InsufficientFundsException(saveDto.getWalletId(), saveDto.getAmount()));

    String massage = "Недостаточно средств на счёте %s для списания суммы %s".formatted(saveDto.getWalletId(), saveDto.getAmount());
    performUpdateAndExpect(saveDto, 409, massage);
  }

  @Test
  void updateWallet_shouldReturnBadRequest_ifInvalidOperationType() throws Exception {
    SaveWalletDto saveDto = buildSaveWalletDto(UUID.randomUUID(), BigDecimal.valueOf(100.0), null);
    Mockito.when(walletService.updateWallet(any(SaveWalletDto.class)))
      .thenThrow(new InvalidOperationTypeException(null));

    String validationMessage = messageSource.getMessage("default.valid.notNull", null, Locale.getDefault());
    performUpdateAndExpect(saveDto, 400, "operationType=[%s]".formatted(validationMessage));
  }

  @Test
  void updateWallet_shouldReturnBadRequest_ifRequestInvalid() throws Exception {
    SaveWalletDto saveDto = new SaveWalletDto();

    mockMvc.perform(post(getPath(PathUtils.UPDATE))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(saveDto)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").exists())
      .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  void updateWallet_shouldReturnBadRequest_ifInvalidJson() throws Exception {
    String invalidJson = "{Невалидный JSON}";
    mockMvc.perform(post(getPath(PathUtils.UPDATE))
        .contentType(MediaType.APPLICATION_JSON)
        .content(invalidJson))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(Matchers.containsString("Невалидный JSON")))
      .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  void updateWallet_shouldReturnBadRequest_ifAmountNegative() throws Exception {
    SaveWalletDto saveDto = buildSaveWalletDto(UUID.randomUUID(), BigDecimal.valueOf(-10.0), OperationType.DEPOSIT);
    String validationMessage = messageSource.getMessage("wallet.valid.amountMin", null, Locale.getDefault());
    mockMvc.perform(post(getPath(PathUtils.UPDATE))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(saveDto)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value(Matchers.containsString("amount=[%s]".formatted(validationMessage))))
      .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  void updateWallet_shouldReturnInternalServerError_ifUnexpectedException() throws Exception {
    SaveWalletDto saveDto = buildSaveWalletDto(UUID.randomUUID(), BigDecimal.valueOf(100.0), OperationType.DEPOSIT);
    Mockito.when(walletService.updateWallet(any(SaveWalletDto.class)))
      .thenThrow(new RuntimeException());

    mockMvc.perform(post(getPath(PathUtils.UPDATE))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(saveDto)))
      .andExpect(status().isInternalServerError())
      .andExpect(jsonPath("$.message").value("Внутренняя ошибка сервиса"))
      .andExpect(jsonPath("$.status").value(500));
  }

  @Test
  void updateWallet_shouldReturnBadRequest_ifIllegalArgumentException() throws Exception {
    SaveWalletDto saveDto = buildSaveWalletDto(UUID.randomUUID(), BigDecimal.valueOf(100.0), OperationType.DEPOSIT);
    Mockito.when(walletService.updateWallet(any(SaveWalletDto.class)))
      .thenThrow(new IllegalArgumentException("Validation message"));

    mockMvc.perform(post(getPath(PathUtils.UPDATE))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(saveDto)))
      .andExpect(status().isInternalServerError())
      .andExpect(jsonPath("$.message").value("Validation message"))
      .andExpect(jsonPath("$.status").value(500));
  }

  @Test
  void updateWallet_shouldReturnBadRequest_ifDataIntegrityViolationExceptionThrown() throws Exception {
    SaveWalletDto saveDto = buildSaveWalletDto(UUID.randomUUID(), BigDecimal.valueOf(100.0), OperationType.DEPOSIT);

    Mockito.when(walletService.updateWallet(any(SaveWalletDto.class)))
      .thenThrow(new DataIntegrityViolationException("Constraint violation"));

    mockMvc.perform(post(getPath(PathUtils.UPDATE))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(saveDto)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Ошибка сохранения данных"))
      .andExpect(jsonPath("$.status").value(400));
  }

  @Test
  void getBalance_shouldReturnWalletBalance() throws Exception {
    UUID walletId = UUID.randomUUID();
    Wallet wallet = buildWallet(walletId, BigDecimal.valueOf(500.0));
    WalletDto walletDto = buildWalletDto(walletId, wallet.getAmount());

    Mockito.when(walletService.getBalance(walletId)).thenReturn(wallet);
    Mockito.when(walletMapper.toDto(wallet)).thenReturn(walletDto);

    mockMvc.perform(get(getPath(PathUtils.GET_BALANCE), walletId))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.walletId").value(walletId.toString()))
      .andExpect(jsonPath("$.amount").value(wallet.getAmount()));
  }

  @Test
  void getBalance_shouldReturnNotFound_ifWalletDoesNotExist() throws Exception {
    UUID walletId = UUID.randomUUID();
    Mockito.when(walletService.getBalance(walletId))
      .thenThrow(new EntityNotFoundException(EntityType.WALLET, walletId));

    mockMvc.perform(get(getPath(PathUtils.GET_BALANCE), walletId))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(404))
      .andExpect(jsonPath("$.message").exists());
  }

  private void performUpdateAndExpect(SaveWalletDto saveDto, int expectedStatus, String expectedMessagePart) throws Exception {
    mockMvc.perform(post(getPath(PathUtils.UPDATE))
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(saveDto)))
      .andExpect(status().is(expectedStatus))
      .andExpect(jsonPath("$.message").value(Matchers.containsString(expectedMessagePart)));
  }

  private String getPath(String path) {
    return String.format("/v1/%s", path);
  }
}
