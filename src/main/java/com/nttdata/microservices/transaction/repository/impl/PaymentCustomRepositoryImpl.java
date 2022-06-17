package com.nttdata.microservices.transaction.repository.impl;

import com.nttdata.microservices.transaction.entity.Payment;
import com.nttdata.microservices.transaction.repository.PaymentCustomRepository;
import com.nttdata.microservices.transaction.util.QueryUtil;
import java.util.Map;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import reactor.core.publisher.Mono;

public class PaymentCustomRepositoryImpl implements PaymentCustomRepository {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  public PaymentCustomRepositoryImpl(ReactiveMongoTemplate reactiveMongoTemplate) {
    this.reactiveMongoTemplate = reactiveMongoTemplate;
  }

  @Override
  public Mono<Double> getSumByCreditId(String creditId) {
    Aggregation aggregation = QueryUtil.getAggregationSum(creditId);
    return reactiveMongoTemplate.aggregate(aggregation, Payment.class, Map.class)
        .map((row) -> (Double) row.get("total"))
        .singleOrEmpty();
  }
}
