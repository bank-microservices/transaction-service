package com.nttdata.microservices.transaction.service.mapper;

import com.nttdata.microservices.transaction.entity.Consumption;
import com.nttdata.microservices.transaction.entity.credit.Credit;
import com.nttdata.microservices.transaction.service.dto.ConsumptionDto;
import com.nttdata.microservices.transaction.service.dto.CreditDto;
import com.nttdata.microservices.transaction.service.mapper.base.EntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ConsumptionMapper extends EntityMapper<ConsumptionDto, Consumption> {

  @Mapping(target = "credit", source = "credit", qualifiedByName = "creditConsumption")
  ConsumptionDto toDto(Consumption entity);

  @Named("creditConsumption")
  CreditDto toCreditDto(Credit entity);

}
