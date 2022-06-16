package com.nttdata.microservices.transaction.service.mapper;

import com.nttdata.microservices.transaction.entity.Payment;
import com.nttdata.microservices.transaction.entity.credit.Credit;
import com.nttdata.microservices.transaction.service.dto.CreditDto;
import com.nttdata.microservices.transaction.service.dto.PaymentDto;
import com.nttdata.microservices.transaction.service.mapper.base.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDto, Payment> {

  @Mapping(target = "credit", source = "credit", qualifiedByName = "creditConsumption")
  PaymentDto toDto(Payment entity);

  @Named("creditConsumption")
  CreditDto toCreditDto(Credit entity);

}
