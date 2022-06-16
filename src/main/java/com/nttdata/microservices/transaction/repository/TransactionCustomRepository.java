package com.nttdata.microservices.transaction.repository;

import com.nttdata.microservices.transaction.entity.Transaction;
import reactor.core.publisher.Flux;

public interface TransactionCustomRepository {

  Flux<Transaction> findByAccountNumber(String accountNumber);

  Flux<Transaction> findByAccountId(String accountId);
}
