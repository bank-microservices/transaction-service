package com.nttdata.microservices.transaction.repository;

import com.nttdata.microservices.transaction.entity.Consumption;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ConsumptionRepository
    extends ReactiveMongoRepository<Consumption, String>, ConsumptionCustomRepository {

  @Aggregation(pipeline = {"{'$match':{'credit._id':?0}}"})
  Flux<Consumption> findByCreditId(String id);

  @Aggregation(pipeline = {"{'$match':{'credit.accountNumber':?0}}"})
  Flux<Consumption> findByCreditAccountNumber(String accountNumber);
}
