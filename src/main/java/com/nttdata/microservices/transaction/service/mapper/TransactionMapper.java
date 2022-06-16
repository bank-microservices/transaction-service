package com.nttdata.microservices.transaction.service.mapper;

import com.nttdata.microservices.transaction.entity.Transaction;
import com.nttdata.microservices.transaction.service.dto.TransactionDto;
import com.nttdata.microservices.transaction.service.mapper.base.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper extends EntityMapper<TransactionDto, Transaction> {

}
