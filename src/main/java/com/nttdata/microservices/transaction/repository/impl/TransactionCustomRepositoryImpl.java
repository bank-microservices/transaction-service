package com.nttdata.microservices.transaction.repository.impl;

import com.nttdata.microservices.transaction.entity.Transaction;
import com.nttdata.microservices.transaction.repository.TransactionCustomRepository;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import reactor.core.publisher.Flux;

public class TransactionCustomRepositoryImpl implements TransactionCustomRepository {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  public TransactionCustomRepositoryImpl(ReactiveMongoTemplate reactiveMongoTemplate) {
    this.reactiveMongoTemplate = reactiveMongoTemplate;
  }

  @Override
  public Flux<Transaction> findByAccountNumber(String accountNumber) {
    MatchOperation match = Aggregation.match(Criteria.where("account.accountNumber").is(accountNumber));
    Aggregation aggregation = Aggregation.newAggregation(match);
    return reactiveMongoTemplate.aggregate(aggregation, Transaction.class, Transaction.class);
  }

  @Override
  public Flux<Transaction> findByAccountId(String accountId) {
    MatchOperation match = Aggregation.match(Criteria.where("account._id").is(accountId));
    Aggregation aggregation = Aggregation.newAggregation(match);
    return reactiveMongoTemplate.aggregate(aggregation, Transaction.class, Transaction.class);
  }

}
