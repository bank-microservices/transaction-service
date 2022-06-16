package com.nttdata.microservices.transaction.repository.impl;

import com.nttdata.microservices.transaction.entity.Consumption;
import com.nttdata.microservices.transaction.repository.ConsumptionCustomRepository;
import com.nttdata.microservices.transaction.util.QueryUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class ConsumptionCustomRepositoryImpl implements ConsumptionCustomRepository {

  private final ReactiveMongoTemplate reactiveMongoTemplate;

  public ConsumptionCustomRepositoryImpl(ReactiveMongoTemplate reactiveMongoTemplate) {
    this.reactiveMongoTemplate = reactiveMongoTemplate;
  }

  @Override
  public Mono<Double> getSumByCreditId(String creditId) {
    Aggregation aggregation = QueryUtil.getAggregationSum(creditId);
    return reactiveMongoTemplate.aggregate(aggregation, Consumption.class, Map.class)
            .map((row) -> (Double) row.get("total"))
            .singleOrEmpty();
  }


}
