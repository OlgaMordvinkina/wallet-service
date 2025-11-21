package ru.itk.wallet_service.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itk.wallet_service.AbstractApplicationTest;
import ru.itk.wallet_service.db.entity.Wallet;
import ru.itk.wallet_service.db.enums.OperationType;
import ru.itk.wallet_service.db.repository.WalletRepository;
import ru.itk.wallet_service.handler.exception.EntityNotFoundException;
import ru.itk.wallet_service.handler.exception.InsufficientFundsException;
import ru.itk.wallet_service.web.dto.SaveWalletDto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest extends AbstractApplicationTest {

  @Mock
  private WalletRepository repository;
  @InjectMocks
  private WalletServiceImpl service;
  @Captor
  private ArgumentCaptor<Wallet> walletCaptor;

  @Test
  void createWallet_shouldCreateWithZeroBalanceAndSave() {
    Wallet saved = buildWallet(UUID.randomUUID(), BigDecimal.ZERO);

    when(repository.save(any(Wallet.class))).thenReturn(saved);

    Wallet result = service.createWallet();

    verify(repository).save(walletCaptor.capture());
    Wallet captured = walletCaptor.getValue();

    assertThat(captured.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    assertThat(result).isSameAs(saved);
  }

  @Test
  void updateWallet_deposit_shouldAddAmountAndSave() {
    UUID id = UUID.randomUUID();
    Wallet existing = buildWallet(id, BigDecimal.valueOf(100.00));

    SaveWalletDto dto = mock(SaveWalletDto.class);
    when(dto.getWalletId()).thenReturn(id);
    when(dto.getOperationType()).thenReturn(OperationType.DEPOSIT);
    when(dto.getAmount()).thenReturn(BigDecimal.valueOf(25.50));

    when(repository.findByIdForUpdate(id)).thenReturn(Optional.of(existing));
    when(repository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

    Wallet updated = service.updateWallet(dto);

    assertThat(updated.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(125.50));
    verify(repository).findByIdForUpdate(id);
    verify(repository).save(existing);
  }

  @Test
  void updateWallet_withdraw_shouldSubtractWhenEnoughFunds() {
    UUID id = UUID.randomUUID();
    Wallet existing = buildWallet(id, BigDecimal.valueOf(100.00));

    SaveWalletDto dto = mock(SaveWalletDto.class);
    when(dto.getWalletId()).thenReturn(id);
    when(dto.getOperationType()).thenReturn(OperationType.WITHDRAW);
    when(dto.getAmount()).thenReturn(BigDecimal.valueOf(40.00));

    when(repository.findByIdForUpdate(id)).thenReturn(Optional.of(existing));
    when(repository.save(any(Wallet.class))).thenAnswer(inv -> inv.getArgument(0));

    Wallet updated = service.updateWallet(dto);

    assertThat(updated.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(60.00));
    verify(repository).save(existing);
  }

  @Test
  void updateWallet_withdraw_shouldThrowInsufficientFundsWhenNotEnough() {
    UUID id = UUID.randomUUID();
    Wallet existing = buildWallet(id, BigDecimal.valueOf(10.00));

    SaveWalletDto dto = mock(SaveWalletDto.class);
    when(dto.getWalletId()).thenReturn(id);
    when(dto.getOperationType()).thenReturn(OperationType.WITHDRAW);
    when(dto.getAmount()).thenReturn(BigDecimal.valueOf(20.00));

    when(repository.findByIdForUpdate(id)).thenReturn(Optional.of(existing));

    assertThatThrownBy(() -> service.updateWallet(dto))
      .isInstanceOf(InsufficientFundsException.class)
      .hasMessageContaining(id.toString());

    verify(repository, never()).save(any());
  }

  @Test
  void updateWallet_unknownOrNullOperation_shouldThrowInvalidOperationTypeException() {
    UUID id = UUID.randomUUID();
    Wallet existing = buildWallet(id, BigDecimal.valueOf(100.00));

    SaveWalletDto dto = mock(SaveWalletDto.class);
    when(dto.getWalletId()).thenReturn(id);
    when(dto.getOperationType()).thenReturn(null);

    when(repository.findByIdForUpdate(id)).thenReturn(Optional.of(existing));

    assertThatThrownBy(() -> service.updateWallet(dto))
      .isInstanceOf(RuntimeException.class);
  }

  @Test
  void updateWallet_notFoundShouldThrowEntityNotFoundException() {
    UUID id = UUID.randomUUID();

    SaveWalletDto dto = mock(SaveWalletDto.class);
    when(dto.getWalletId()).thenReturn(id);

    when(repository.findByIdForUpdate(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.updateWallet(dto))
      .isInstanceOf(EntityNotFoundException.class)
      .hasMessageContaining(id.toString());
  }

  @Test
  void getBalance_shouldReturnWallet_whenFound() {
    UUID id = UUID.randomUUID();
    Wallet wallet = buildWallet(id, BigDecimal.valueOf(500.00));

    when(repository.findById(id)).thenReturn(Optional.of(wallet));

    Wallet result = service.getBalance(id);

    assertThat(result).isSameAs(wallet);
  }

  @Test
  void getBalance_shouldThrowEntityNotFound_whenNotFound() {
    UUID id = UUID.randomUUID();

    when(repository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getBalance(id))
      .isInstanceOf(EntityNotFoundException.class)
      .hasMessageContaining(id.toString());
  }
}