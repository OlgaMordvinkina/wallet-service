package ru.itk.wallet_service.service;

import ru.itk.wallet_service.db.entity.Wallet;
import ru.itk.wallet_service.web.dto.SaveWalletDto;

import java.util.UUID;

public interface WalletService {
  Wallet createWallet();

  Wallet updateWallet(SaveWalletDto dto);

  Wallet getBalance(UUID walletId);
}
