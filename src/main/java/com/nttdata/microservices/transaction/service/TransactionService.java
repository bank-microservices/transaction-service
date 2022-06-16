package com.nttdata.microservices.transaction.service;

import com.nttdata.microservices.transaction.service.dto.TransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TransactionService {

  Flux<TransactionDto> findAll();

  Flux<TransactionDto> findByAccountId(String accountId);

  Flux<TransactionDto> findByAccountNumber(String accountNumber);

  Mono<TransactionDto> deposit(TransactionDto transactionDto);

  Mono<TransactionDto> withdraw(TransactionDto transactionDto);

}
