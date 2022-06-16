package com.nttdata.microservices.transaction.repository;

import com.nttdata.microservices.transaction.entity.Transaction;
import com.nttdata.microservices.transaction.service.dto.TransactionDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface TransactionRepository extends ReactiveMongoRepository<Transaction, String>, TransactionCustomRepository {

}
