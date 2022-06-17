package com.nttdata.microservices.transaction.repository;

import com.nttdata.microservices.transaction.entity.Transaction;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TransactionRepository
    extends ReactiveMongoRepository<Transaction, String>, TransactionCustomRepository {

}
