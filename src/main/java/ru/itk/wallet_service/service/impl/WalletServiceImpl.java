package ru.itk.wallet_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itk.wallet_service.db.entity.Wallet;
import ru.itk.wallet_service.db.repository.WalletRepository;
import ru.itk.wallet_service.enums.EntityType;
import ru.itk.wallet_service.handler.exception.EntityNotFoundException;
import ru.itk.wallet_service.handler.exception.InsufficientFundsException;
import ru.itk.wallet_service.handler.exception.InvalidOperationTypeException;
import ru.itk.wallet_service.service.WalletService;
import ru.itk.wallet_service.web.dto.SaveWalletDto;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
  private final WalletRepository repository;

  @Override
  @Transactional
  public Wallet createWallet() {
    Wallet wallet = new Wallet();
    wallet.setAmount(BigDecimal.ZERO);
    return repository.save(wallet);
  }

  @Transactional
  @Override
  public Wallet updateWallet(SaveWalletDto dto) {
    Wallet wallet = findByIdForUpdate(dto);

    BigDecimal currentAmount = wallet.getAmount();
    BigDecimal newAmount = switch (dto.getOperationType()) {
      case DEPOSIT -> currentAmount.add(dto.getAmount());
      case WITHDRAW -> {
        if (currentAmount.compareTo(dto.getAmount()) < 0) {
          throw new InsufficientFundsException(wallet.getWalletId(), dto.getAmount());
        }
        yield currentAmount.subtract(dto.getAmount());
      }
      default -> throw new InvalidOperationTypeException(dto.getOperationType());
    };

    wallet.setAmount(newAmount);
    return repository.save(wallet);
  }

  /**
   * Получение кошелька с блокировкой на запись, чтобы избежать race condition
   */
  private Wallet findByIdForUpdate(SaveWalletDto dto) {
    return repository.findByIdForUpdate(dto.getWalletId())
      .orElseThrow(() -> new EntityNotFoundException(EntityType.WALLET, dto.getWalletId()));
  }

  @Transactional(readOnly = true)
  @Override
  public Wallet getBalance(UUID walletId) {
    return repository.findById(walletId)
      .orElseThrow(() -> new EntityNotFoundException(EntityType.WALLET, walletId));
  }
}
