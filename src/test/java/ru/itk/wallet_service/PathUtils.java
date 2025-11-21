package ru.itk.wallet_service;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathUtils {
  public static final String CREATE = "wallet/create";
  public static final String UPDATE = "wallet";
  public static final String GET_BALANCE = "wallets/{walletId}";
}
