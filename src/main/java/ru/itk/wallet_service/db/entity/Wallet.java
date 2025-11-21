package ru.itk.wallet_service.db.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet {

  @Id
  @GeneratedValue
  @Column(name = "wallet_id", updatable = false, nullable = false)
  UUID walletId;

  @Column(name = "amount", precision = 10, scale = 2)
  BigDecimal amount;
}
