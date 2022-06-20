package com.nttdata.microservices.transaction.service.mapper;

import com.nttdata.microservices.transaction.entity.Consumption;
import com.nttdata.microservices.transaction.service.dto.ConsumptionDto;
import com.nttdata.microservices.transaction.service.mapper.base.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsumptionMapper extends EntityMapper<ConsumptionDto, Consumption> {

}
