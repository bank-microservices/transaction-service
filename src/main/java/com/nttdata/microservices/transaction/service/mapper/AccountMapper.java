package com.nttdata.microservices.transaction.service.mapper;

import com.nttdata.microservices.transaction.entity.account.Account;
import com.nttdata.microservices.transaction.service.dto.AccountDto;
import com.nttdata.microservices.transaction.service.mapper.base.EntityMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper extends EntityMapper<AccountDto, Account> {

}

