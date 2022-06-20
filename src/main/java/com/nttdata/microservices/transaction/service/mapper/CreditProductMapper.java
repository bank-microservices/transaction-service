package com.nttdata.microservices.transaction.service.mapper;

import com.nttdata.microservices.transaction.entity.credit.CreditProduct;
import com.nttdata.microservices.transaction.service.dto.CreditProductDto;
import com.nttdata.microservices.transaction.service.mapper.base.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CreditProductMapper extends EntityMapper<CreditProductDto, CreditProduct> {

}
