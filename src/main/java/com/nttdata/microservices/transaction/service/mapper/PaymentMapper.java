package com.nttdata.microservices.transaction.service.mapper;

import com.nttdata.microservices.transaction.entity.Payment;
import com.nttdata.microservices.transaction.service.dto.PaymentDto;
import com.nttdata.microservices.transaction.service.mapper.base.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper extends EntityMapper<PaymentDto, Payment> {

}
