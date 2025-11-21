package ru.itk.wallet_service.mapper;

import org.mapstruct.Mapper;
import ru.itk.wallet_service.db.entity.Wallet;
import ru.itk.wallet_service.web.dto.WalletDto;

@Mapper(componentModel = "spring")
public interface WalletMapper {

  WalletDto toDto(Wallet entity);

}
