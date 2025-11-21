package ru.itk.wallet_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;
import ru.itk.wallet_service.db.entity.Wallet;
import ru.itk.wallet_service.db.enums.OperationType;
import ru.itk.wallet_service.web.dto.SaveWalletDto;
import ru.itk.wallet_service.web.dto.WalletDto;

import java.math.BigDecimal;
import java.util.UUID;

public class AbstractApplicationTest {
  @Autowired
  protected MessageSource messageSource;
  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  protected ObjectMapper objectMapper;

  protected Wallet buildWallet(UUID walletId, BigDecimal amount) {
    Wallet entity = new Wallet();
    entity.setWalletId(walletId);
    entity.setAmount(amount);
    return entity;
  }

  protected WalletDto buildWalletDto(UUID walletId, BigDecimal amount) {
    WalletDto dto = new WalletDto();
    dto.setWalletId(walletId);
    dto.setAmount(amount);
    return dto;
  }

  protected SaveWalletDto buildSaveWalletDto(UUID walletId, BigDecimal amount, OperationType type) {
    SaveWalletDto saveDto = new SaveWalletDto();
    saveDto.setWalletId(walletId);
    saveDto.setAmount(amount);
    saveDto.setOperationType(type);
    return saveDto;
  }
}
