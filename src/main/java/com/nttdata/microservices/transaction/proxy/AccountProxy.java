package com.nttdata.microservices.transaction.proxy;


import com.nttdata.microservices.transaction.service.dto.AccountDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountProxy {

  Flux<AccountDto> findByAccountNumberAndClientDocument(String accountNumber,
                                                        String documentNumber);

  Mono<AccountDto> findByAccountNumber(String accountNumber);


  Mono<AccountDto> updateAccountAmount(String accountId, double amount);
}
